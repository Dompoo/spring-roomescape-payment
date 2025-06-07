package roomescape.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.global.exception.business.ExternalApiException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import static roomescape.global.exception.business.BusinessErrorCode.PAYMENT_APPROVE_FAILED;

public class PaymentErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<String> sensitiveErrorCodes = List.of(
            "INVALID_API_KEY",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT"
    );

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        byte[] bodyBytes = StreamUtils.copyToByteArray(response.getBody());
        JsonNode jsonNode = objectMapper.readTree(new ByteArrayInputStream(bodyBytes));
        final String code = extractMessage(jsonNode, "code");
        if (isSensitiveError(code)) {
            throw new ExternalApiException(PAYMENT_APPROVE_FAILED);
        }
        final String errMessage = extractMessage(jsonNode, "message");
        throw new ExternalApiException(PAYMENT_APPROVE_FAILED, errMessage);
    }

    private boolean isSensitiveError(String code) {
        return sensitiveErrorCodes.contains(code);
    }

    private String extractMessage(JsonNode jsonNode, String key) {
        return jsonNode.get(key).asText();
    }
}

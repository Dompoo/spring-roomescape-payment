const PENDING_API_ENDPOINT = '/api/admin/reservations/pending';

document.addEventListener('DOMContentLoaded', () => {
    fetch(PENDING_API_ENDPOINT)
        .then(response => {
            if (response.status === 200) return response.json();
            throw new Error('Read failed');
        })
        .then(render)
        .catch(error => console.error('Error fetching reservations:', error));
});

function render(data) {
    const tableBody = document.getElementById('table-body');
    tableBody.innerHTML = '';

    data.forEach(item => {
        const row = tableBody.insertRow();

        const id = item.id;
        const name = item.member.name;
        const theme = item.theme.themeName;
        const date = item.date;
        const startAt = item.time.startAt;

        row.insertCell(0).textContent = id;            // 예약 대기 id
        row.insertCell(1).textContent = name;          // 예약자명
        row.insertCell(2).textContent = theme;         // 테마명
        row.insertCell(3).textContent = date;          // 예약 날짜
        row.insertCell(4).textContent = startAt;       // 시작 시간

        const actionCell = row.insertCell(row.cells.length);

        // actionCell.appendChild(createActionButton('승인', 'btn-primary', approve));
        actionCell.appendChild(createActionButton('거절', 'btn-danger', deny));
    });
}

// function approve(event) {
//     const row = event.target.closest('tr');
//     const id = row.cells[0].textContent;
//
//     /*
//     TODO: [4단계] 예약 대기 목록 관리 기능
//           예약 대기 승인 API 호출
//      */
//     const endpoint = '' + id;
//     return fetch(endpoint, {
//         method: ''
//     }).then(response => {
//         if (response.status === 200) return;
//         throw new Error('Delete failed');
//     }).then(() => location.reload());
// }

function deny(event) {
    const row = event.target.closest('tr');
    const id = row.cells[0].textContent;

    return fetch(`/api/admin/reservations/pending/${id}/deny`, {
        method: 'DELETE'
    }).then(response => {
        if (response.status === 200) return;
        throw new Error('Delete failed');
    }).then(() => location.reload());
}

function createActionButton(label, className, eventListener) {
    const button = document.createElement('button');
    button.textContent = label;
    button.classList.add('btn', className, 'mr-2');
    button.addEventListener('click', eventListener);
    return button;
}

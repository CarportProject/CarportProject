const modal = document.getElementById('orderModal');
const closeBtn = document.getElementById('modalClose');
const toggle = document.getElementById('discountTypeToggle');
const labelPct = document.getElementById('label-pct');
const labelKr = document.getElementById('label-kr');
const applyBtn = document.getElementById('applyDiscount');
const discountVal = document.getElementById('discountValue');
const discountDisplay = document.getElementById('discount-display');
const totalDisplay = document.getElementById('total-display');
const totalPriceInput = document.getElementById('total-price-input');

let basePrice = 0;

function fmtKr(n) {
    return n.toLocaleString('da-DK') + ' kr.';
}

function openModal(row) {
    document.getElementById('modal-order-id').value = row.dataset.id;
    document.getElementById('modal-name').textContent = row.dataset.name || '–';
    document.getElementById('modal-email').textContent = row.dataset.email || '–';
    document.getElementById('modal-phone').textContent = row.dataset.phone || '–';
    document.getElementById('modal-address').textContent = row.dataset.address || '–';
    document.getElementById('modal-comment').value = row.dataset.remark || '';

    basePrice = parseInt(row.dataset.price) || 0;
    totalPriceInput.value = basePrice;
    discountVal.value = '';
    toggle.checked = false;
    labelPct.classList.add('active');
    labelKr.classList.remove('active');
    discountDisplay.textContent = 'Rabat – 0 kr.';
    totalDisplay.textContent = 'Total – ' + fmtKr(basePrice);

    fetch('/admin/material-list/' + row.dataset.id)
        .then(response => response.json())
        .then(materials => {
            const tbody = document.getElementById('styklisteBody');
            const empty = document.getElementById('styklisteEmpty');
            const table = document.getElementById('styklisteTable');
            tbody.innerHTML = '';

            if (materials.length === 0) {
                empty.style.display = 'block';
                table.style.display = 'none';
            } else {
                empty.style.display = 'none';
                table.style.display = 'table';
                materials.forEach(m => {
                    tbody.innerHTML += `<tr>
                        <td>${m.material.name}</td>
                        <td>${m.amount} stk.</td>
                        <td>${m.description}</td>
                    </tr>`;
                });
            }
        })
        .catch(() => {
            document.getElementById('styklisteEmpty').textContent = 'Kunne ikke hente stykliste';
        });

    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
}

function closeModal() {
    modal.classList.remove('active');
    document.body.style.overflow = '';
}

document.querySelectorAll('.order-row').forEach(row => {
    row.addEventListener('click', () => openModal(row));
});

closeBtn.addEventListener('click', closeModal);

modal.addEventListener('click', e => {
    if (e.target === modal) closeModal();
});

document.addEventListener('keydown', e => {
    if (e.key === 'Escape') closeModal();
});

toggle.addEventListener('change', () => {
    if (toggle.checked) {
        labelPct.classList.remove('active');
        labelKr.classList.add('active');
    } else {
        labelPct.classList.add('active');
        labelKr.classList.remove('active');
    }
    discountVal.value = '';
    discountVal.placeholder = '0';
    discountDisplay.textContent = 'Rabat – 0 kr.';
    totalDisplay.textContent = 'Total – ' + fmtKr(basePrice);
});

applyBtn.addEventListener('click', () => {
    const val = parseFloat(discountVal.value) || 0;
    let rabatKr = 0;

    if (toggle.checked) {
        rabatKr = Math.min(val, basePrice);
    } else {
        const pct = Math.min(Math.max(val, 0), 100);
        rabatKr = Math.round(basePrice * pct / 100);
    }

    const total = basePrice - rabatKr;
    discountDisplay.textContent = 'Rabat – –' + fmtKr(rabatKr);
    totalDisplay.textContent = 'Total – ' + fmtKr(total);
    totalPriceInput.value = total;
});

const statusSelect = document.querySelector('.status-select');
if (statusSelect) {
    statusSelect.addEventListener('change', function () {
        this.form.submit();
    });
}

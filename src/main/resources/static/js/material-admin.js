const modal = document.getElementById('materialModal');
const closeBtn = document.getElementById('modalClose');
const cancelBtn = document.getElementById('modalCancel');

document.querySelectorAll('.material-row').forEach(row => {
    row.addEventListener('click', () => {
        document.getElementById('modal-id').value = row.dataset.id;
        document.getElementById('modal-name').value = row.dataset.name || '';
        document.getElementById('modal-description').value = row.dataset.description || '';
        document.getElementById('modal-price').value = row.dataset.price || '';
        document.getElementById('modal-length-min').value = row.dataset.minLength || '';
        document.getElementById('modal-length-max').value = row.dataset.maxLength || '';
        modal.classList.add('active');
        document.body.style.overflow = 'hidden';
    });
});

function closeModal() {
    modal.classList.remove('active');
    document.body.style.overflow = '';
}

closeBtn.addEventListener('click', closeModal);
cancelBtn.addEventListener('click', closeModal);
modal.addEventListener('click', e => { if (e.target === modal) closeModal(); });
document.addEventListener('keydown', e => { if (e.key === 'Escape') closeModal(); });

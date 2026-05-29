const konamiCode = [
    'ArrowUp', 'ArrowUp',
    'ArrowDown', 'ArrowDown',
    'ArrowLeft', 'ArrowRight',
    'ArrowLeft', 'ArrowRight',
    'b', 'a'
];

let index = 0;

document.addEventListener('keydown', (e) => {
    if (e.key === konamiCode[index]) {
        index++;
        if (index === konamiCode.length) {
            activateCheats();
            index = 0;
        }
    } else {
        index = 0;
    }
});

function activateCheats() {
    fetch('/konami', { method: 'POST' })
        .then(res => {
            if (res.ok) window.location.href = '/';
        });
}

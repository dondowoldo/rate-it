document.addEventListener('DOMContentLoaded', () => {
    const categorySplide = new Splide('#splide-category', {
        autoWidth: true,
        autoHeight: true,
        gap: '0.5rem',
        type: 'slide',
        drag: 'free',
        arrows: false,
        loop: false,
        pagination: false
    });
    categorySplide.mount();
});
document.addEventListener('DOMContentLoaded', () => {
    const splide = new Splide('#splide-interests', {
        fixedWidth: '10rem',
        fixedHeight: '11rem',
        gap: '1rem',
        type: 'slide',
        drag: 'free',
        arrows: false,
        loop: false
    });
    splide.mount();
    loadInterests();

    const categorySplide = new Splide('#splide-category', {
        autoWidth: true,
        autoHeight: true,
        gap: '0.5rem',
        type: 'slide',
        drag: 'free',
        arrows: false,
        loop: false,
        pagination: false,
        autoScroll: {pauseOnHover: true, speed: 0.1}
    });
    categorySplide.mount(window.splide.Extensions);
});
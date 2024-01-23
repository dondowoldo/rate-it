document.addEventListener('DOMContentLoaded', () => {
    const splide = new Splide('.splide', {
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
});
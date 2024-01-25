// https://medium.com/geekculture/how-to-build-a-simple-star-rating-system-abcbb5117365

const STARS = 10;

function addClassToStars(stars, rating, className) {
    //loop through and set the active class on preceding stars
    for (let i = 1; i <= STARS; i++) {
        if (i <= rating) {
            //check if the classlist contains the active class, if not, add the class
            if (!stars.querySelector('.star-' + i).classList.contains(className)) {
                stars.querySelector('.star-' + i).classList.add(className);
            }
        } else {
            //check if the classlist contains the active class, if yes, remove the class
            if (stars.querySelector('.star-' + i).classList.contains(className)) {
                stars.querySelector('.star-' + i).classList.remove(className);
            }
        }
    }
}

function initializeRating() {
    let placeRatingStars = document.querySelectorAll('.place-rating-star');

    placeRatingStars.forEach(function (star) {
        function handleStarEvent(event) {
            event.preventDefault(); // Prevent default behavior for touch events

            let stars = event.currentTarget.parentElement;
            let rating = parseInt(event.currentTarget.getAttribute("data-star"));

            switch (event.type) {
                case 'mouseover':
                    stars.classList.add('hover');
                    addClassToStars(stars, rating, 'hover-active');
                    break;

                case 'mouseout':
                    stars.classList.remove('hover');
                    for (let j = 1; j <= STARS; j++) {
                        stars.querySelector('.star-' + j).classList.remove('hover-active');
                    }
                    break;

                case 'click':
                case 'touchstart':
                    // Set the input value and add class based on the click rating
                    let inputClick = stars.parentElement.querySelector('input');
                    inputClick.value = rating;
                    addClassToStars(stars, rating, 'is-active');
                    break;

                case 'touchmove':
                    stars.classList.add('hover');

                    // Get touch position
                    let touchX = (event.type === 'touchmove') ? event.touches[0].clientX : event.clientX;

                    // Calculate the relative position within the stars container
                    let starsRect = stars.getBoundingClientRect();
                    let relativeX = touchX - starsRect.left;

                    // Calculate the star rating based on the relative position
                    let starWidth = starsRect.width / STARS;
                    let newRating = Math.ceil(relativeX / starWidth);

                    // Ensure the newRating is within the range of 0 to 10
                    newRating = Math.max(0, Math.min(newRating, 10));

                    // If newRating is 0, set it to null
                    newRating = (newRating === 0) ? null : newRating;

                    // Set the input value and add class based on the touchmove rating
                    let inputMove = stars.parentElement.querySelector('input');
                    inputMove.value = newRating;
                    addClassToStars(stars, newRating, 'hover-active');
                    break;
            }
        }

        star.addEventListener('mouseover', handleStarEvent);
        star.addEventListener('mouseout', handleStarEvent);
        star.addEventListener('click', handleStarEvent);

        star.addEventListener('touchstart', handleStarEvent);
        star.addEventListener('touchmove', handleStarEvent);
    });
}

document.addEventListener('DOMContentLoaded', initializeRating);
document.addEventListener('DOMContentLoaded', function () {

    async function handleSubmit(event) {
        try {
            event.preventDefault();
            const formData = new FormData(event.target);
            const ratings = {};

            for (const rating of formData) {
                let id = rating[0];
                const value = rating[1];
                const idMatch = id.match(/ratings\[(\d+)\]/);

                if (idMatch) {
                    id = idMatch[1];
                }

                ratings[id] = value;
            }

            const response = await fetch(`/api/v1/places/${placeId}/rate`, {
                method: 'POST',
                body: JSON.stringify({ratings}),
                headers: {
                    'Content-Type': 'application/json'
                },
            });

            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }

            const responseData = await response.json();

            const updateElement = (elementId, value) => {
                const element = document.getElementById(elementId);
                if (value === null) {
                    element.textContent = 'N/A';
                } else {
                    element.textContent = (value / 2).toFixed(1);
                }
            };

            responseData.criteria.forEach(criteria => {
                updateElement(criteria.id, criteria.avgRating);
            });

            updateElement('place-rating', responseData.avgRating);

        } catch (error) {
            console.error(error);
        }
    }

    const form = document.getElementById("form");
    form.addEventListener('submit', handleSubmit);
});
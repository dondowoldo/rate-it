document.addEventListener('DOMContentLoaded', function () {
});

function toggleInterest(userInterest) {
    const isExpanded = userInterest.classList.toggle('expanded');

    if (isExpanded) {
        loadAndFormatPlaces(userInterest, 3);
    }
}

function loadAndFormatPlaces(userInterest, limit) {
    const placesContainer = userInterest.querySelector('.user-interest-places');

    if (placesContainer) {

        const ulElements = placesContainer.querySelectorAll('ul');

        for (let i = 0; i < Math.min(limit, ulElements.length); i++) {
            const ulElement = ulElements[i];
            const avgRatingSpan = ulElement.querySelector('.place-rating-average');
            const avgRating = parseFloat(avgRatingSpan.textContent);

            if (!isNaN(avgRating)) {
                avgRatingSpan.textContent = (avgRating / 2).toFixed(1);
            }

            ulElement.style.display = 'flex';
        }
    }
}

function handleDetailsClick(event) {
    event.stopPropagation();
}

const detailsButtons = document.querySelectorAll('.user-interest-extension .button');
detailsButtons.forEach(function (button) {
    button.addEventListener('click', handleDetailsClick);
});


document.addEventListener('DOMContentLoaded', function () {
    const ratingSpans = document.querySelectorAll('.rating');

    ratingSpans.forEach(ratingSpan => {
        const rating = parseFloat(ratingSpan.textContent);
        if (!isNaN(rating)) {
            ratingSpan.textContent = rating.toFixed(1);
        }
    });
});

function toggleExtension(toggleDiv) {
    const isExtended = toggleDiv.classList.toggle('extended');

    if (isExtended && toggleDiv.classList.contains("user-interest")) {
        loadAndFormatItems(toggleDiv, 3);
    } else if (isExtended && toggleDiv.classList.contains("user-detail-place")) {
        loadAndFormatItems(toggleDiv, 0);
    }
}

function loadAndFormatItems(toggleDiv, maxItems) {
    const extensionContainer = toggleDiv.querySelector('.extension-list');

    if (extensionContainer) {

        const ulElements = extensionContainer.querySelectorAll('.user-interest-place');
        let limit;

        if (maxItems === 3) {
            limit = Math.min(ulElements.length, 3)
        } else if (maxItems === 0) {
            limit = ulElements.length;
        }

        for (let i = 0; i < limit; i++) {
            const ulElement = ulElements[i];
            const avgRatingSpan = ulElement.querySelector('.place-rating-average');
            const avgRating = parseFloat(avgRatingSpan.textContent);

            if (!isNaN(avgRating)) {
                avgRatingSpan.textContent = avgRating.toFixed(1);
            }

            ulElement.style.display = 'flex';
        }
    }
}

function handleDetailsClick(event) {
    event.stopPropagation();
}

const detailsButtons = document.querySelectorAll('.extension .button');
detailsButtons.forEach(function (button) {
    button.addEventListener('click', handleDetailsClick);
});


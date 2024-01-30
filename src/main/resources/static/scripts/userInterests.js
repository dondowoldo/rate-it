function toggleInterest(userInterest) {
    const isExpanded = userInterest.classList.toggle('expanded');
    if (isExpanded) {
        formatAverageRatings();
    }
}

function formatAverageRatings() {
    const avgRatingSpans = document.querySelectorAll('.place-rating-average');
    avgRatingSpans.forEach(avgRatingSpan => {
        const avgRating = parseFloat(avgRatingSpan.textContent);

        if (!isNaN(avgRating)) {
            avgRatingSpan.textContent = (avgRating / 2).toFixed(1);
        }
    });
}
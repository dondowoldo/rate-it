document.addEventListener('DOMContentLoaded', function () {
    var expandButtons = document.getElementsByClassName('expand-button');

    for (var i = 0; i < expandButtons.length; i++) {
        expandButtons[i].addEventListener('click', function () {
            var userName = this.id.replace('button-show-ratings-', '');
            toggleRatings(userName);
        });
    }
});

function toggleRatings(userName) {
    var ratingsList = document.getElementById('ratingsList_' + userName);
    var expandButton = document.getElementById('button-show-ratings-' + userName);

    ratingsList.style.display = (ratingsList.style.display === 'none') ? 'block' : 'none';

    if (ratingsList.style.display === 'none') {
        expandButton.querySelector('img').src = '/icons/right-arrow.svg';
    } else {
        expandButton.querySelector('img').src = '/icons/down-arrow.svg';
    }
}

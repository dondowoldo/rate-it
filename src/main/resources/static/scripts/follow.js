function followUser(userId, follows) {
    $.ajax({
        type: 'POST',
        url: '/api/v1/users/' + userId + '/follow',
        contentType: 'application/json',
        data: JSON.stringify({follow: !follows}),
        success: function () {
            const followersCount = document.querySelector('.user-follow a:first-child');
            const followButton = document.querySelector('.button');
            const currentFollowers = parseInt(followersCount.textContent, 10);

            followersCount.textContent = ((currentFollowers + (follows ? 1 : -1)) + ' Followers');
            followButton.textContent = follows ? 'Unfollow' : 'Follow';

            follows = !follows;
        },
        error: function () {
            const errorMessage = document.createElement('p');
            errorMessage.className = 'error';
            errorMessage.textContent = "Sorry, an error occurred";

            const main = document.querySelector('.content');
            const userFollow = document.querySelector('.user-follow');

            if (main && userFollow) {
                main.insertBefore(errorMessage, userFollow.nextSibling);
            }
        }
    });
}

document.addEventListener('DOMContentLoaded', function () {
    const userBio = document.getElementsByClassName('user-bio');

    // Set initial content to the user's bio
    let initialBio = userBio.textContent;

    userBio.addEventListener('input', function () {
        // Update the initialBio when the content changes
        initialBio = userBio.textContent;
    });
});

function saveBio() {
    const userBio = document.getElementsByClassName('user-bio');
    const newBio = userBio.textContent;

    // Send the newBio to the server for saving (you need to implement this part)

    // For now, let's log the newBio to the console
    console.log('New Bio:', newBio);
}

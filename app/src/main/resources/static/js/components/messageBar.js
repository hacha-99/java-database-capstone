export function triggerMessage(msg, red_green_orange) {
    const messageBar = document.querySelector("#message");

    if (messageBar.style.visibility === "visible") {
        messageBar.textContent += ` & ${msg}`;
        messageBar.scrollTop = messageBar.scrollHeight;
        return;
    } else {
        messageBar.replaceChildren(msg);
    }

    if (red_green_orange === "red") {
        messageBar.style.backgroundColor = "rgb(190, 35, 35)";
    } else if (red_green_orange === "green") {
        messageBar.style.backgroundColor = "rgb(35, 127, 52)";
    } else if (red_green_orange === "orange") {
        messageBar.style.backgroundColor = "rgb(240, 139, 15)";
    }

    messageBar.style.opacity = "1";
    messageBar.style.visibility = "visible";
    messageBar.style.top = "0";

    setTimeout(() => {
        messageBar.style.top = "-7.5rem";
    }, 3000); // setTimeout is fire and forget?

    setTimeout(() => {
        messageBar.style.opacity = "0";
        messageBar.style.visibility = "hidden";
        messageBar.style.pointerEvents = "none"
    }, 3500);
}
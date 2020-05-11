( () => {
	"use-strict";

	const emailField = document.forms[0][0];
	const passwordField = document.forms[0][1];
	const rememberCheckBox = document.forms[0][2];
	const loginButton = document.forms[0][3];
	const warning = $("#warning-info");

	const NOT_FOUND = 404, OK = 200;

	function echoWarning() {
		warning.show(500, () => {
			setTimeout( () => {
				warning.hide(500);
			}, 1000);
		});
	}

	loginButton.onclick = () => {
		let request = new XMLHttpRequest();

		request.onreadystatechange = () => {
			if(request.readyState === 4) {
				if(request.status === OK) {
					location.assign("/home");
				}
				else {
					echoWarning();
				}
			}
		};

		request.open("POST", `${window.location.origin}/session/signin`, true);
		request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		request.send(`email=${emailField.value}&password=${passwordField.value}&remember=${rememberCheckBox.checked}`);
	};
})();
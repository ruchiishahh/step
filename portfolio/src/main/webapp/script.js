// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Fetches a name from the server and adds it to the DOM.
 */
function getNameUsingArrowFunctions() {
    fetch('/data').then(response => response.text()).then((name) => {
        document.getElementById('dataComment-container').innerText = name;
    });
}

/**
 * Fetches stats from the servers and adds them to the DOM.
 */
function getDataComment() {
    fetch('/data').then(response => response.json()).then((dataComments) => {
        document.getElementById("dataComment-container").innerText = "";
        const commentContainer = document.getElementById("dataComment-container");
        const numOfCommentsToDisplay = document.getElementById("numComments").value;
        dataComments.splice(0, numOfCommentsToDisplay).forEach(dataComment => { 
            let miniP = document.createElement("p");
            miniP.innerText = dataComment.message + " | Author: " + dataComment.creator;
            commentContainer.appendChild(miniP);
        })
    });
}

const places = [
    ['Cupertino High School', 37.3194, -122.0091, 'This is where I went to high school.'],
    ['El Amigo Burrito', 37.3238, -121.9809, 'This is my favorite Mexican restaurant.'],
    ['Tpumps', 37.3121, -122.0318, 'This is my favorite Boba Shop.'],
    ['Santa Cruz Beach', 36.9741, -122.0308, 'This is my favorite beach. We go here every summer.'],
    ['Salon Professional', 37.3711, 121.9256, 'This is my favorite Salon to get a haircut.']
];

/** Creates a map that shows landmarks around Google. */
function createMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 37.3194, lng: -122.0091}, zoom: 15});

    for (let i = 0; i < places.length; i++) {
        let place = places[i];
        addLandmark(map, place[1], place[2], place[0], place[3])
    }

}

/** Adds a marker that shows an info window when clicked. */
function addLandmark(map, lat, lng, title, description) {
  const marker = new google.maps.Marker(
      {position: {lat: lat, lng: lng}, map: map, title: title});

  const infoWindow = new google.maps.InfoWindow({content: description});
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}

/**
 * Deletes the dataComments from the servers and reflect on DOM.
 */
function deleteDataComments(){
    fetch('/deletedatacomments', {
        method: 'POST',
    }).then(getDataComments());
}

/**
 * Checks log-in Status & hides comments by default. 
 */
async function loadWebPage() {
    const inputForm = document.getElementById("input-container");
    const logContainer = document.getElementById("login-container");
    const loginLink = document.getElementById("admin-user-link");
    getDataComment();
    const currLogStatus = await getLogStatus();
    const checkLoggedIn = currLogStatus.checkIfLoggedIn;
    if (checkLoggedIn) {
        loginLink.innerHTML = "Logout here!";
        logContainer.style.display = "block";
        inputForm.style.display = "block";
    } else {
        loginLink.innerHTML = "Please login!";
        logContainer.style.display = "block";
    }
    loginLink.href = currLogStatus.linkForLoginLogout;
}

/**
 * Fetches log-in status from the servlet
 */
async function getLogStatus() {
    const response = await fetch('/user-log');
    const loggerStatus = await response.json();
    return loggerStatus;
}

/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings = ['Fav Show: White Collar', 'Fav Song: Saturday Nights', 'Fav Color: Blue', 'Fav Sport: Basketball', 'Fav Food: Enchiladas'];


  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}

const TxtType = function(el, toRotate, period) {
    this.toRotate = toRotate;
    this.el = el;
    this.loopNum = 0;
    this.period = parseInt(period, 10) || 2000;
    this.txt = '';
    this.tick();
    this.isDeleting = false;
};

/**
 * Create the typewrite functionality.
 */
TxtType.prototype.tick = function() {
    const i = this.loopNum % this.toRotate.length;
    const fullTxt = this.toRotate[i];

    if (this.isDeleting) {
        this.txt = fullTxt.substring(0, this.txt.length - 1);
    } else {
        this.txt = fullTxt.substring(0, this.txt.length + 1);
    }

    this.el.innerHTML = '<span class="wrap">'+this.txt+'</span>';
    let delta = 200 - Math.random() * 100;

    if (this.isDeleting) {
        delta /= 2;
    }

    if (!this.isDeleting && this.txt === fullTxt) {
        delta = this.period;
        this.isDeleting = true;
    } else if (this.isDeleting && this.txt === '') {
        this.isDeleting = false;
        this.loopNum++;
        delta = 500;
    }
    
    let that = this;
    setTimeout(function() {
        that.tick();
    }, delta);
};

/**
 * Rotates different texts for the typewrite
*/

window.onload = function() {
    const elements = document.getElementsByClassName('typewrite');
    for (let i=0; i<elements.length; i++) {
        const toRotate = elements[i].getAttribute('data-type');
        const period = elements[i].getAttribute('data-period');
        if (toRotate) {
            new TxtType(elements[i], JSON.parse(toRotate), period);
        }
    }
    const css = document.createElement("style");
    css.type = "text/css";
    css.innerHTML = ".typewrite > .wrap { border-right: 0.08em solid #000}";
    document.body.appendChild(css);
};
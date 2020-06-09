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

/**
 * Animates the markers onto the map
 */
function toggleBounce() {
    if (marker.getAnimation() !== null) {
        marker.setAnimation(null);
    } else {
        marker.setAnimation(google.maps.Animation.BOUNCE);
    }
}

/**
 * Creates the map and sets the markers.
 */
function createMap() {
    const map = new google.maps.Map(document.getElementById('map'), {
        zoom: 12,
        center: {lat: 37.3230, lng: -122.0322},
    });
    setMarkers(map);
}

const places = [
    ['High School', 37.3194, -122.0091, 4],
    ['Fav Restaurant', 37.3238, -121.9809, 5],
    ['Fav Boba Shop', 37.3121, -122.0318, 3],
    ['Fav Beach', 36.9741, -122.0308, 2],
    ['Fav Salon', 37.3711, 121.9256, 1]
];

/**
 * Sets markers on the map based on assigned coordinates.
 */
function setMarkers(map) {
    const image = {
        url: 'https://developers.google.com/maps/documentation/javascript/examples/full/images/beachflag.png',
        size: new google.maps.Size(20, 32),
        origin: new google.maps.Point(0, 0),
        anchor: new google.maps.Point(0, 32)
    };
    
    const shape = {
        coords: [1, 1, 1, 20, 18, 20, 18, 1],
        type: 'poly'
    };
    
    for (let i = 0; i < places.length; i++) {
        let place = places[i];
        let marker = new google.maps.Marker({
            position: {lat: place[1], lng: place[2]},
            map: map,
            icon: image,
            shape: shape,
            title: place[0],
            zIndex: place[3]
        });
        marker.addListener('click', toggleBounce);
    }
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
    const getLog = (await getLogStatus() !== 'false');
    if (getLog) {
        loginLink.href = "/_ah/logout?continue=%2F"
        loginLink.innerHTML = "Logout here!";
        logContainer.style.display = "block";
        inputForm.style.display = "block";
    } else {
        loginLink.href = "/_ah/login?continue=%2F"
        loginLink.innerHTML = "Please Login!";
        logContainer.style.display = "block";
    }
}

/**
 * Fetches log-in status from the servlet
 */
async function getLogStatus() {
    const response = await fetch('/user-log');
    const isLoggedIn = await response.text();
    return isLoggedIn;
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
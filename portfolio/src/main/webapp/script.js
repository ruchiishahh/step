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
      const commentContainer = document.getElementById("dataComment-container");
      dataComments.forEach(dataComment => { 
          let miniP = document.createElement("p");
          miniP.innerText = "Basketball Player: " + dataComment.message;
          commentContainer.appendChild(miniP);
      })
  });
}

function deleteDataComments(){
    fetch('/deletedatacomments', {
        method: 'POST',
    }).then(getDataComments());
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


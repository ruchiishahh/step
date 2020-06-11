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

package com.google.sps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //some basic if statements
    if(request.getAttendees().isEmpty()){
        //when there are no attendees
        return Arrays.asList(TimeRange.WHOLE_DAY);
    } else if(request.getDuration() > TimeRange.END_OF_DAY) {
        //when the meeting duration is longer than a day
        return Arrays.asList();
    }
    
    //list of TimeRanges that will not work for meeting attendees
    Collection<TimeRange> blockedTimes = new ArrayList();

    //traverse through all attendees that need to attend the meeting
    for(String attendee: request.getAttendees()) {
        //traverse through all of the events occuring
        for(Event event: events){
            //check if that attendee is attending that event
            if(event.getAttendees().contains(attendee)) {
                //must block off that TimeRange
                blockedTimes.add(event.getWhen());
            }
        }
    }
    
    //track the next possible available time
    int nextAvailableTime = TimeRange.START_OF_DAY;
    //keep a list of all the available TimeRanges
    Collection<TimeRange> availableTimes = new ArrayList();

    //sort the blockedTimes by startTime
    Collections.sort((List)blockedTimes, TimeRange.ORDER_BY_START);

    //in ascending order, add the available TimeRanges into the availableTimes list 
    for(TimeRange bt: blockedTimes) {
        if(bt.start() > nextAvailableTime && ((bt.start() - nextAvailableTime) >= request.getDuration())) {
            availableTimes.add(TimeRange.fromStartEnd(nextAvailableTime, bt.start(), false));
            nextAvailableTime = bt.end();
        } else if (bt.start() > nextAvailableTime) {
            nextAvailableTime = bt.end();
        } else if (bt.end() > nextAvailableTime) {
            nextAvailableTime = bt.end();
        }
    }

    if((TimeRange.END_OF_DAY - nextAvailableTime) >= request.getDuration()){
        availableTimes.add(TimeRange.fromStartEnd(nextAvailableTime, TimeRange.END_OF_DAY, true));  
    }
    return availableTimes;
  }
}
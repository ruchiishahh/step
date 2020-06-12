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

    // Handle edge cases: when there are no attendees & the meeting duration is longer than a day
    if(request.getAttendees().isEmpty()) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    } else if(request.getDuration() > TimeRange.END_OF_DAY) {
        return Arrays.asList();
    }
    
    // List of TimeRanges that will not work for meeting attendees
    Collection<TimeRange> blockedTimes = new ArrayList();

    // Add the unavailable TimeRanges into the blockedTimes list
    for(String attendee: request.getAttendees()) {
        for(Event event: events){
            if(event.getAttendees().contains(attendee)) {
                blockedTimes.add(event.getWhen());
            }
        }
    }
    
    // Track the next possible available time
    int nextAvailableTime = TimeRange.START_OF_DAY;
    // Keep a list of all the available TimeRanges
    Collection<TimeRange> availableTimes = new ArrayList();

    // Sort the blockedTimes by startTime
    Collections.sort((List)blockedTimes, TimeRange.ORDER_BY_START);

    // In ascending order, add the available TimeRanges into the availableTimes list 
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

    // Last check to add the rest of the available TimeRange into the availableTimes list
    if((TimeRange.END_OF_DAY - nextAvailableTime) >= request.getDuration()){
        availableTimes.add(TimeRange.fromStartEnd(nextAvailableTime, TimeRange.END_OF_DAY, true));  
    }
    
    return availableTimes;
  }
}
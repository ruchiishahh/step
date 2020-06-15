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
    if(request.getAttendees().isEmpty() && request.getOptionalAttendees().isEmpty()) {
        return Arrays.asList(TimeRange.WHOLE_DAY);
    } else if(request.getDuration() > TimeRange.END_OF_DAY) {
        return Arrays.asList();
    }
    
    Collection<String> allAttendees = new ArrayList();
    allAttendees.addAll(request.getAttendees());
    allAttendees.addAll(request.getOptionalAttendees());

    // List of TimeRanges that will not work for meeting attendees
    Collection<TimeRange> blockedTimes = createListOfBlockedTimes(events, request.getAttendees());
    // List of TimeRanges that will not work for meeting attendees + optional attendees
    Collection<TimeRange> optionalBlockedTimes = createListOfBlockedTimes(events, allAttendees);

    
    // List of TimeRanges that will work for meeting attendees + optional attendees
    Collection<TimeRange> optionalAvailableTimes = createListOfAvailableTimes(events, request, optionalBlockedTimes);
    // List of TimeRanges that will work for meeting attendees
    Collection<TimeRange> availableTimes = new ArrayList();
    if (optionalAvailableTimes.size() == 0) {
        availableTimes = createListOfAvailableTimes(events, request, blockedTimes);
    }
    

    // Only return the List that works for optional attendees if there is one or more TimeRange or no attendees
    if (optionalAvailableTimes.size() > 0) {
        return optionalAvailableTimes;
    } else if (optionalAvailableTimes.size() == 0 && (request.getAttendees()).size() == 0) {
        return optionalAvailableTimes;
    }
    return availableTimes;
  }

  /**
  * Create a List of TimeRanges that will not work for the given attendees based on events that are scheduled.
  */
  public Collection<TimeRange> createListOfBlockedTimes(Collection<Event> events, Collection<String> totalAttendees) {
    // List of TimeRanges that will not work for given meeting attendees
    Collection<TimeRange> blockedTimes = new ArrayList();
    // Add the unavailable TimeRanges into the blockedTimes list
    for(String attendee: totalAttendees) {
        for(Event event: events){
            if(event.getAttendees().contains(attendee)) {
                blockedTimes.add(event.getWhen());
            }
        }
    }
    return blockedTimes;
  }
  
  /**
  * Create a List of TimeRanges that will work for the given attendees based on events that are scheduled.
  */
  public Collection<TimeRange> createListOfAvailableTimes(Collection<Event> events, MeetingRequest request, Collection<TimeRange> blockedTimes) {
    // List of TimeRanges that will work for given attendees
    Collection<TimeRange> availableTimes = new ArrayList();
    // Track the next possible available time
    int nextAvailableTime = TimeRange.START_OF_DAY;
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
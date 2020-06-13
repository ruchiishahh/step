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
    
    // List of TimeRanges that will not work for meeting attendees
    Collection<TimeRange> blockedTimes = new ArrayList();
    // List of TimeRanges that will not work for meeting attendees + optional attendees
    Collection<TimeRange> optionalBlockedTimes = new ArrayList();
    
    createListOfBlockedTimes(events, request, blockedTimes, request.getAttendees());
    List<String> allAttendees = new ArrayList();
    allAttendees.addAll(request.getAttendees());
    allAttendees.addAll(request.getOptionalAttendees());
    createListOfBlockedTimes(events, request, optionalBlockedTimes, allAttendees);

    // List of TimeRanges that will work for meeting attendees
    Collection<TimeRange> availableTimes = new ArrayList();
    // List of TimeRanges that will work for meeting attendees + optional attendees
    Collection<TimeRange> optionalAvailableTimes = new ArrayList();

    createListOfAvailableTimes(events, request, blockedTimes, availableTimes);
    createListOfAvailableTimes(events, request, optionalBlockedTimes, optionalAvailableTimes);

    // Only return the List that works for optional attendees if there is one or more TimeRange
    if (optionalAvailableTimes.size() > 0) {
        return optionalAvailableTimes;
    }
    return availableTimes;
  }

  /**
  * Create a List of TimeRanges that will not work for the given attendees based on events that are scheduled.
  */
  public void createListOfBlockedTimes(Collection<Event> events, MeetingRequest request, Collection<TimeRange> blockedTimes, Collection<String> totalAttendees) {
    // Add the unavailable TimeRanges into the blockedTimes list
    for(String attendee: totalAttendees) {
        for(Event event: events){
            if(event.getAttendees().contains(attendee)) {
                blockedTimes.add(event.getWhen());
            }
        }
    }
  }
  
  /**
  * Create a List of TimeRanges that will work for the given attendees based on events that are scheduled.
  */
  public void createListOfAvailableTimes(Collection<Event> events, MeetingRequest request, Collection<TimeRange> blockedTimes, Collection<TimeRange> availableTimes) {
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
  }
}
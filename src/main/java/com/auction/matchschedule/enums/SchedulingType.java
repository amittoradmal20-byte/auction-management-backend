
package com.auction.matchschedule.enums;

public enum SchedulingType {

    /**
     * Matches scheduled every day.
     */
    DAILY,

    /**
     * Matches scheduled on selected weekdays.
     * Example: Monday, Wednesday and Friday.
     */
    WEEKLY,

    /**
     * Matches scheduled on Saturday, Sunday,
     * or both, depending on configuration.
     */
    WEEKEND,

    /**
     * Tournament completed within two
     * consecutive or selected playing days.
     */
    TWO_DAY,

    /**
     * Tournament completed within three
     * consecutive or selected playing days.
     */
    THREE_DAY,

    /**
     * Organizer selects specific dates.
     */
    CUSTOM

}

package es.jdl.holidayapi.domain;

import java.time.LocalDate;

/**
 * Date interval used by filters
 */
public class DateInterval {
    private LocalDate from;
    private LocalDate to;

    public DateInterval() {}

    public DateInterval(LocalDate from, LocalDate to) {
        this.from = from;
        this.to = to;
    }

    public LocalDate getFrom() {
        return from;
    }

    public void setFrom(LocalDate from) {
        this.from = from;
    }

    public LocalDate getTo() {
        return to;
    }

    public void setTo(LocalDate to) {
        this.to = to;
    }
}

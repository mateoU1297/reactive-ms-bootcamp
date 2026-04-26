package com.pragma.ms_bootcamp.domain.spi;

import com.pragma.ms_bootcamp.domain.model.Bootcamp;

public interface IReportClientPort {
    void notifyBootcampCreated(Bootcamp bootcamp);
}

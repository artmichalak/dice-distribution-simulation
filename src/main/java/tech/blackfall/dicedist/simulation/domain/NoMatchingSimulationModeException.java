package tech.blackfall.dicedist.simulation.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class NoMatchingSimulationModeException extends RuntimeException {

}

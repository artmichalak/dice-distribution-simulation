package tech.blackfall.dicedist.simulation.domain;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
class DistributionNotFoundException extends RuntimeException {

  public DistributionNotFoundException(int dice, int sides) {
    super("Distribution not found for dice=" + dice + " and sides=" + sides);
  }
}

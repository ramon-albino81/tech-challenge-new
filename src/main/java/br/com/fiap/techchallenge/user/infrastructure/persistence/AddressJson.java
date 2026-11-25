package br.com.fiap.techchallenge.user.infrastructure.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AddressJson(
        @JsonProperty("street") String street,
        @JsonProperty("number") String number,
        @JsonProperty("city") String city,
        @JsonProperty("postal_code") String postalCode
) {
}

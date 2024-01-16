package nl.tudelft.sem.template.example.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import nl.tudelft.sem.template.example.dtos.generic.GenericResponse;

@Data
@AllArgsConstructor
public class PrivacySettingResponse implements GenericResponse {
    private final boolean enableCollection;
}

package filipednb.github.com.hostfullyapi.domain.block;

import filipednb.github.com.hostfullyapi.domain.property.PropertyEntity;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BlockResponse {

    private Long id;

    private PropertyEntity property;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

}

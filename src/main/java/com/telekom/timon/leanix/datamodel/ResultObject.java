package com.telekom.timon.leanix.datamodel;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class ResultObject {

    private String name;
    private String displayName;
    private String leanixId;
    private String description;

}

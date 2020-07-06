package com.telekom.timon.leanix.datamodel;

import lombok.*;
import org.apache.commons.lang3.StringUtils;

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

    public String getPrefixOnly(){
        if(StringUtils.isNoneEmpty(name)){
            return name.substring(0,name.indexOf(": ")).trim();
        }
        return name;
    }
}

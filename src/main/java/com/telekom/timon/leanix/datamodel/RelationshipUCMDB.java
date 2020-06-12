package com.telekom.timon.leanix.datamodel;

import lombok.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class RelationshipUCMDB implements Comparable<RelationshipUCMDB> {

    private String startRel;
    private String endRel;


    public List<String> getUCMDBAsXlsData() {
        List<String> ucmdbRelations = new ArrayList<>();
        ucmdbRelations.add(startRel);
        ucmdbRelations.add("containment");
        ucmdbRelations.add(endRel);
        return ucmdbRelations;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final RelationshipUCMDB that = (RelationshipUCMDB) o;

        return new EqualsBuilder()
                .append(startRel, that.startRel)
                .append(endRel, that.endRel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(startRel)
                .append(endRel)
                .toHashCode();
    }


    @Override
    public int compareTo(final RelationshipUCMDB relationshipUCMDB) {
        return this.getStartRel().compareTo(relationshipUCMDB.getStartRel());
    }

}

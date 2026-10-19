/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import Common.Logger;
import Common.ReaderUtil;

/**
 *
 * @author andras
 */
public class VariableTypeText extends Logger {

    private Integer groupKey, dataI2;

    public VariableTypeText() {
    }

    public Integer getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(Integer groupKey) {
        this.groupKey = groupKey;
    }

    public Integer getDataI2() {
        return dataI2;
    }

    public void setDataI2(Integer dataI2) {
        this.dataI2 = dataI2;
    }

    public VariableTypeText(Integer groupKey, Integer dataI2) {
        this.groupKey = groupKey;
        this.dataI2 = dataI2;
    }

    public boolean readValues(ReaderUtil ru) {
        groupKey = ru.readInt(false);
        dataI2 = ru.readInt(false);

        return (this.groupKey != null && this.dataI2 != null);
    }

    public VariableTypeText duplicate() {
        VariableTypeText ret = new VariableTypeText();

        ret.dataI2 = dataI2;
        ret.groupKey = groupKey;

        return ret;
    }

    @Override
    public String toString() {
        return String.format("VariableTypeText: groupKey: %x (%d), dataI2: %d (%x)", groupKey, groupKey, dataI2, dataI2);
    }

}

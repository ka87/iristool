/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Common.DataType;

import java.util.ArrayList;

/**
 *
 * @author andra
 * @param <E>
 */
public class ArrayListWithSizeOverLoad<E> extends ArrayList {

    private Integer customSize = null;

    public Integer getCustomSize() {
        if (customSize == null) {
            return this.size();
        }
        return customSize;
    }

    public void setCustomSize(Integer customSize) {
        this.customSize = customSize;
    }
    
    @Override
    public E get(int i) {
        return (E) super.get(i);
    }
}

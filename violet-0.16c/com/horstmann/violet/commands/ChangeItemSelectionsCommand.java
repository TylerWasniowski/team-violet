package com.horstmann.violet.commands;

import com.horstmann.violet.framework.Node;
import com.horstmann.violet.framework.UniquelyIdentifiable;
import com.horstmann.violet.graphs.TeamDiagram;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by CSingh on 5/5/2017.
 */
public class ChangeItemSelectionsCommand implements Command {
    
    private static final long serialVersionUID = -5173365451099251363L;
    private String clientGraphID;
    private Set<String> idsOfSelectedItems;
    
    public ChangeItemSelectionsCommand(String clientGraphID, Set<UniquelyIdentifiable> selectedItems) {
        this.clientGraphID = clientGraphID;

        // Populate idsOfSelectedItems set with the ids of the given selectedItems
        idsOfSelectedItems = new HashSet<>();
        selectedItems.forEach((UniquelyIdentifiable selectedItem) -> {
            idsOfSelectedItems.add(selectedItem.getID());
        });
    }

    @Override
    public boolean execute(TeamDiagram graphToExecuteCommandOn) {
        Set<UniquelyIdentifiable> selectedItems = new HashSet<>();
        for (String selectedItemID: idsOfSelectedItems) {
            UniquelyIdentifiable item = graphToExecuteCommandOn.findItemFromID(selectedItemID);

            if (item != null)
                selectedItems.add(item);
            else
                return false;

        }

        graphToExecuteCommandOn.putItemSelections(clientGraphID, selectedItems);
        return true;
    }
}
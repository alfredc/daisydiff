/*
 * Copyright 2007 Guy Van den Broeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.outerj.daisy.diff.html.modification;

import java.util.List;

public class Modification implements Cloneable {

    private ModificationType type;

    private ModificationType outputType;

    // Holds LEFT or RIGHT (for 3-way diffs)
    private ModificationType side;

    private long id = -1;

    private int matchId = -1;

    private Modification prevMod = null;

    private Modification nextMod = null;

    private boolean firstOfID = false;

    private List<HtmlLayoutChange> htmlLayoutChanges = null;

    // For 3-way diffs
    public Modification(ModificationType type, ModificationType outputType, ModificationType side, int matchId) {
        this.type = type;
        this.outputType = outputType;
        this.side = side;
        this.matchId = matchId;
    }

    public Modification(ModificationType type, ModificationType outputType) {
        this.type = type;
        this.outputType = outputType;
        this.side = ModificationType.NONE; // 2-way diffs
    }

    @Override
    public Modification clone() {
        Modification newM = new Modification(this.getType(), getOutputType(), getSide(), getMatchId());
        newM.setID(getID());
        newM.setChanges(getChanges());
        newM.setHtmlLayoutChanges(getHtmlLayoutChanges());
        newM.setFirstOfID(isFirstOfID());
        newM.setNext(getNext());
        newM.setPrevious(getPrevious());
        return newM;
    }

    public ModificationType getType() {
        return type;
    }

    /**
     * Returns the type of this modification regarding output formatting (i.e.
     * in order to specify how this modification shall be formatted). 
     * 
     * In three-way diffs we format "ADDED" modifications as REMOVED, and the
     * other way round, because the comparison is reversed, compared to a 
     * two-way diff.
     * @return the way how this modification shall be formatted
     */
    public ModificationType getOutputType() {
    	return outputType;
    }

    /**
     * Returns the side (LEFT or RIGHT) that this modification was made on in a
     * 3-way diff. In a 2-way diff, this should be ModificationType.NONE.
     * @return the side this modification was made on
     */
    public ModificationType getSide() {
      return side;
    }
    
    public void setID(long id) {
        this.id = id;
    }

    public long getID() {
        return id;
    }

    public int getMatchId() {
        return matchId;
    }

    public void setPrevious(Modification m) {
        this.prevMod = m;
    }

    public Modification getPrevious() {
        return prevMod;
    }

    public void setNext(Modification m) {
        this.nextMod = m;
    }

    public Modification getNext() {
        return nextMod;
    }

    private String changes;

    public void setChanges(final String changes) {
        this.changes = changes;
    }

    public String getChanges() {
        return changes;
    }

    public boolean isFirstOfID() {
        return firstOfID;
    }

    public void setFirstOfID(boolean firstOfID) {
        this.firstOfID = firstOfID;
    }

	/**
	 * @return the htmlLayoutChanges
	 */
	public List<HtmlLayoutChange> getHtmlLayoutChanges() {
		return htmlLayoutChanges;
	}

	/**
	 * @param htmlLayoutChanges the htmlLayoutChanges to set
	 */
	public void setHtmlLayoutChanges(List<HtmlLayoutChange> htmlLayoutChanges) {
		this.htmlLayoutChanges = htmlLayoutChanges;
	}

	
    
    

}

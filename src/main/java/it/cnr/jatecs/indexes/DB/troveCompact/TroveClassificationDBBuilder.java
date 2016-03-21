/*
 * This file is part of JaTeCS.
 *
 * JaTeCS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JaTeCS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JaTeCS.  If not, see <http://www.gnu.org/licenses/>.
 *
 * The software has been mainly developed by (in alphabetical order):
 * - Andrea Esuli (andrea.esuli@isti.cnr.it)
 * - Tiziano Fagni (tiziano.fagni@isti.cnr.it)
 * - Alejandro Moreo Fernández (alejandro.moreo@isti.cnr.it)
 * Other past contributors were:
 * - Giacomo Berardi (giacomo.berardi@isti.cnr.it)
 */

package it.cnr.jatecs.indexes.DB.troveCompact;

import gnu.trove.TShortArrayList;
import it.cnr.jatecs.indexes.DB.interfaces.ICategoryDB;
import it.cnr.jatecs.indexes.DB.interfaces.IClassificationDB;
import it.cnr.jatecs.indexes.DB.interfaces.IClassificationDBBuilder;
import it.cnr.jatecs.indexes.DB.interfaces.IDocumentDB;
import it.cnr.jatecs.utils.iterators.interfaces.IShortIterator;

import java.util.Vector;

public class TroveClassificationDBBuilder implements
        IClassificationDBBuilder {

    public TroveClassificationDBBuilder(IDocumentDB documentsDB,ICategoryDB categoriesDB) {
        super();
        _classificationDB = new TroveClassificationDB(documentsDB,categoriesDB);
    }

    public TroveClassificationDBBuilder(TroveClassificationDB clDB)
    {
        super();
        _classificationDB = clDB;
    }

    protected TroveClassificationDB _classificationDB;

    public void setDocumentCategory(int document, short category) {
        setDocumentCategory(document, category, true);
    }

    protected void addCategoryHierarchicaly(int document, short category, boolean primary)
    {
        TShortArrayList cats=_classificationDB._documentsCategories.get(document);
        Vector<Boolean> catsPrimary = _classificationDB._documentsCatsPrimary.get(document);
        int pos = cats.binarySearch(category);
        if(pos<0) {
            cats.insert(-pos-1, category);
            catsPrimary.insertElementAt(primary, -pos-1);
        }
        else
        {
            if (primary)
            {
                catsPrimary.set(pos, true);
            }
        }

        IShortIterator parents = _classificationDB.getCategoryDB().getParentCategories(category);
        while(parents.hasNext())
            addCategoryHierarchicaly(document, parents.next(), primary);
    }

    public IClassificationDB getClassificationDB() {
        return _classificationDB;
    }

    public void setDocumentCategory(int document, short category, boolean primary)
    {
        if(document>=0) {
            int size = _classificationDB._documentsCategories.size();
            if(document>=size) {
                for(int i=size;i<=document;++i)
                {
                    _classificationDB._documentsCategories.add(new TShortArrayList());
                    _classificationDB._documentsCatsPrimary.add(new Vector<Boolean>());
                }
            }
            if(category>=0)
                addCategoryHierarchicaly(document, category, primary);
        }
    }
}

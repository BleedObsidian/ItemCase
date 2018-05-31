/*
 * ItemCase is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package com.gmail.bleedobsidian.itemcase.configurations;

import com.gmail.bleedobsidian.itemcase.ConfigurationFile;
import com.gmail.bleedobsidian.itemcase.LanguageTranslator.Language;

/**
 * A configuration file that holds the translations for a specific dialect.
 * 
 * @author Jesse Prescott (BleedObsidian)
 */
public class LanguageFile extends ConfigurationFile {
    
    /**
     * Constructor.
     * 
     * @param Language The language this file is for.
     */
    public LanguageFile(Language language) {
        
        // Give config file name and prevent file being copied outside of jar.
        super("languages/" + language.name() + ".yml", false);
    }
    
    /**
     * Get the raw translation for the given message key.
     * 
     * @param key Key.
     * @return String.
     */
    public String getRawTranslation(String key) {
        
        return this.file.getString(key);
    }
}

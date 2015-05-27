package org.n52.sos.config.sqlite;


import org.n52.iceland.config.AbstractSettingValueFactory;
import org.n52.iceland.config.SettingValue;
import org.n52.iceland.i18n.MultilingualString;
import org.n52.iceland.ogc.gml.time.TimeInstant;
import org.n52.sos.config.sqlite.entities.BooleanSettingValue;
import org.n52.sos.config.sqlite.entities.ChoiceSettingValue;
import org.n52.sos.config.sqlite.entities.FileSettingValue;
import org.n52.sos.config.sqlite.entities.IntegerSettingValue;
import org.n52.sos.config.sqlite.entities.MultilingualStringSettingValue;
import org.n52.sos.config.sqlite.entities.NumericSettingValue;
import org.n52.sos.config.sqlite.entities.StringSettingValue;
import org.n52.sos.config.sqlite.entities.TimeInstantSettingValue;
import org.n52.sos.config.sqlite.entities.UriSettingValue;

/**
 * TODO JavaDoc
 * @author Christian Autermann
 */
public class SQLiteSettingValueFactory extends AbstractSettingValueFactory {

    @Override
    public BooleanSettingValue newBooleanSettingValue() {
        return new BooleanSettingValue();
    }

    @Override
    public IntegerSettingValue newIntegerSettingValue() {
        return new IntegerSettingValue();
    }

    @Override
    public StringSettingValue newStringSettingValue() {
        return new StringSettingValue();
    }

    @Override
    public FileSettingValue newFileSettingValue() {
        return new FileSettingValue();
    }

    @Override
    public UriSettingValue newUriSettingValue() {
        return new UriSettingValue();
    }

    @Override
    protected SettingValue<Double> newNumericSettingValue() {
        return new NumericSettingValue();
    }

    @Override
    protected SettingValue<TimeInstant> newTimeInstantSettingValue() {
        return new TimeInstantSettingValue();
    }

    @Override
    protected SettingValue<MultilingualString> newMultiLingualStringSettingValue() {
        return new MultilingualStringSettingValue();
    }

    @Override
    protected SettingValue<String> newChoiceSettingValue() {
        return new ChoiceSettingValue();
    }

}

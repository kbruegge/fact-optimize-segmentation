import com.google.common.math.DoubleMath;
import stream.Data;
import stream.Processor;
import stream.annotations.Parameter;

import java.util.ArrayList;

/**
 * Calculate the mean of an array and multiply by some factor.
 * Created by kai on 09.02.16.
 */
public class ExampleProcess implements Processor {

    @Parameter(required=true)
    double factor = 0.5;

    @Override
    public Data process(Data data) {
        double[] photons = (double[]) data.get("estimatedPhotons");
        double mean = DoubleMath.mean(photons);
        data.put("mean", mean*factor);
        return data;
    }
}

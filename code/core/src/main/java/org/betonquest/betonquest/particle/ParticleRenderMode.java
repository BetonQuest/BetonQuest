package org.betonquest.betonquest.particle;

import com.destroystokyo.paper.ParticleBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Represents the different ways to render particles.
 */
public enum ParticleRenderMode implements Function<List<List<ParticleBuilder>>, List<ParticleBuilder>> {

    /**
     * Renders particles in the order they are provided.
     */
    FORWARD {
        @Override
        public List<ParticleBuilder> apply(final List<List<ParticleBuilder>> lists) {
            return lists.stream().flatMap(List::stream).toList();
        }
    },
    /**
     * Renders particles in reverse order.
     */
    BACKWARD {
        @Override
        public List<ParticleBuilder> apply(final List<List<ParticleBuilder>> lists) {
            final List<ParticleBuilder> result = new ArrayList<>(FORWARD.apply(lists));
            Collections.reverse(result);
            return result;
        }
    },
    /**
     * Renders particles towards the center.
     */
    INWARD {
        @Override
        public List<ParticleBuilder> apply(final List<List<ParticleBuilder>> lists) {
            final List<ParticleBuilder> forward = FORWARD.apply(lists);
            final List<ParticleBuilder> inward = new ArrayList<>();
            int left = 0;
            int right = forward.size() - 1;
            while (left <= right) {
                inward.add(forward.get(left));
                if (left != right) {
                    inward.add(forward.get(right));
                }
                left++;
                right--;
            }
            return inward;
        }
    },
    /**
     * Renders particles away from the center.
     */
    OUTWARD {
        @Override
        public List<ParticleBuilder> apply(final List<List<ParticleBuilder>> lists) {
            final List<ParticleBuilder> result = new ArrayList<>(INWARD.apply(lists));
            Collections.reverse(result);
            return result;
        }
    }
}

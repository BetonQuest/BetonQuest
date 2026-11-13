---
icon: material/shimmer
tags:
  - Particle
---

# Particles

Particle animations allow you to create complex shapes and effects in your world. 
You can define these animations in the `particles` section of a quest package and use them later with actions like [`spawnparticles`](../Reference/Actions-List.md#spawnparticles).

## Structure

This section shows a complete example with all possible configuration keys.

```yaml title="Complete particle example"
particles:
  complex_effect:
    type: REDSTONE #(1)!
    range: 32 #(2)!
    animation:
      duration: 10 #(3)!
      timeunit: SECONDS #(4)!
      batchsize: 5 #(5)!
      sampling:
        primary: 100 #(6)!
        secondary: 10 #(7)!
      order: BACKWARD #(8)!
      thread: false #(9)!
    shape:
      x: spiral_x #(10)!
      y: spiral_y
      z: spiral_z
      offset:
        x: 0.1 #(11)!
        y: 0.1
        z: 0.1
      count: 1 #(12)!
      extra: 0 #(13)!
    data: #(14)!
      color:
        r: 255
        g: 0
        b: 0
      size: 1
```

1.  **type**: The [Bukkit Particle](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Particle.html) name.
2.  **range**: Maximum distance in blocks players can see the particles.
3.  **duration**: The total length of the animation or undefined (=0) to spawn everything instantly.
4.  **timeunit**: The unit for `duration` (`SECONDS`, `TICKS`, `MINUTES`).
5.  **batchsize**: How many points are calculated and spawned per tick/ste`p.
6.  **primary**: Number of samples for the `d1` parameter. May never be less than 2.
7.  **secondary**: Number of samples for the `d2` parameter. May never be 1 or negative.
8.  **order**: The execution order (`FORWARD`, `BACKWARD`, `INWARD`, `OUTWARD`).
9.  **thread**: Whether to use an own thread for calculation and spawning (default `false`). Recommended for very complex effects.
10. **x, y, z**: The position relative to the origin.
11. **offset**: The randomized spread of particles in each direction.
12. **count**: The number of particles per point.
13. **extra**: Type-specific extra data (e.g. speed).
14. **data**: Additional settings for specific particle types.

??? info "Function Support"

    BetonQuest distinguishes between static settings and dynamic values that can be determined by [Functions](Functions.md). Where a function is allowed, you can either provide a **constant number** (e.g., `5.0`) or the **name of a function** (e.g., `my_function`).
    
    | Section       | Key                                  | Function allowed? |
    |---------------|--------------------------------------|:-----------------:|
    | -             | `type`                               |        ❌         |
    | -             | `range`                              |        ✅         |
    | **animation** | `duration`                           |        ❌         |
    | **animation** | `timeunit`                           |        ❌         |
    | **animation** | `batchsize`                          |        ❌         |
    | **animation** | `sampling` (`primary` & `secondary`) |        ❌         |
    | **animation** | `order`                              |        ❌         |
    | **animation** | `thread`                             |        ❌         |
    | **shape**     | `x`, `y`, `z`                        |        ✅         |
    | **shape**     | `offset` (`x`, `y`, `z`)             |        ✅         |
    | **shape**     | `count`                              |        ✅         |
    | **shape**     | `extra`                              |        ✅         |
    | **data**      | `color` (`r`, `g`, `b`)              |        ✅         |
    | **data**      | `size`                               |        ✅         |
    | **data**      | `transition` (`r`, `g`, `b`)         |        ✅         |
    | **data**      | `item`                               |        ❌         |
    | **data**      | `block`                              |        ❌         |
    | **data**      | `value`                              |        ✅         |
    | **data**      | `vibration` (`time`)                 |        ✅         |
    | **data**      | `vibration` (`unit`)                 |        ❌         |
    | **data**      | `vibration` (`location` / `entity`)  |        ❌         |
    | **data**      | `duration`                           |        ✅         |
    | **data**      | `duration_unit`                      |        ❌         |
    | **data**      | `target`                             |        ❌         |

---

## Animation & Sampling

The `animation` system determines how and when particles appear. It is based on a sampling process where mathematical functions are used to calculate points in space and time.

### Timing Parameters

To understand the flow of the animation, three values are crucial:

*   **duration**: The total length of the animation (in `timeunit`). If this value is 0 or not defined, all particles are spawned simultaneously.
*   **batchsize**: Determines how many points are spawned per calculated time step.
*   **sampling**: Defines the total number of points to be calculated.
*   **thread**: If set to `true`, the spawning of the particles is moved to an asynchronous thread. This prevents the main server thread from being overloaded by complex calculations and removes the minimal batch-delay of 1 tick.

### Sampling Logic & Delta Values

BetonQuest uses so-called "Delta Functions". When you use a [Function](Functions.md) in the particle settings, it is automatically passed one or two progress values:

- **d1 (Primary)**: A value from `0.0` to `1.0`. It represents the progress along the `primary` sampling points.
- **d2 (Secondary)**: A second value from `0.0` to `1.0`. It is only used if `secondary` sampling is defined (greater than 1).

| Sampling Type | Description                                                   | Use Case                                           |
|:--------------|:--------------------------------------------------------------|:---------------------------------------------------|
| **Primary**   | The main loop of the animation.                               | Linear movements, simple shapes.                   |
| **Secondary** | A nested loop within each primary step.                       | Complex surfaces, volumes, or pulsing effects.     |

---

## Particle Data Variants

Some particle types require specific configuration in the `data` section. If the type does not require data, the section can be omitted.

### Dust Particles
Used for particles that require color and size information. You can change the color and size over the course of the animation via functions.

```yaml
type: REDSTONE
data:
  color:
    r: 255 #(1)!
    g: 0   #(2)!
    b: 0   #(3)!
  size: 0.5 #(4)!
```

1.  **r**: Red component (0-255).
2.  **g**: Green component (0-255).
3.  **b**: Blue component (0-255).
4.  **size**: The size of the particle.

### Color Transition
Used for particles that fade from one color to another.

```yaml
type: DUST_COLOR_TRANSITION
data:
  color: #(1)!
    r: 255
    g: 0
    b: 0
  transition: #(2)!
    r: 0
    g: 0
    b: 255
  size: 1.0 #(3)!
```

1.  **color**: The starting color.
2.  **transition**: The target color to fade into.
3.  **size**: The size of the particle.

### Blocks
Used for particles that represent a block (e.g. for break effects).

```yaml
type: BLOCK_DUST
data:
  block: "minecraft:stone" #(1)!
```

1.  **block**: The [Bukkit BlockData](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/block/data/BlockData.html) string.

### Items
Used for particles that represent an item (e.g. for break effects).

```yaml
type: ITEM_CRACK
data:
  item: "iron_sword" #(1)!
```

1.  **item**: An item defined in the `items` section. See [Items](Items.md) for more information.

### Color Particles
!!! tip "@snippet:versions:mc-1.20.6@"

Used for particles that require a color but aren't dust.

```yaml
type: ENTITY_EFFECT
data:
  color: #(1)!
    r: 255 
    g: 255
    b: 0
```

1.  **color**: The color of the particle.

### Numeric Data Particles
!!! tip "@snippet:versions:mc-1.20.6@"

Used for particles that require a single numeric value (integer or float).

```yaml
type: SCULK_CHARGE
data:
  value: 2.0 #(1)!
```

1.  **value**: The numeric data value.

### Vibration Particles
!!! tip "@snippet:versions:mc-1.20.6@"

Used for particles that travel from the origin to a target location or entity.

```yaml
type: VIBRATION
data:
  vibration:
    time: 2 #(1)!
    unit: SECONDS #(2)!
    location: '100;64;100;world' #(3)!
    # entity: '550e8400-e29b-11d4-a716-446655440000' #(4)!
```

1.  **time**: The time it takes for the vibration to arrive at the target.
2.  **unit**: The time unit for `time`.
3.  **location**: The target location.
4.  **entity**: The target entity's UUID.

### Trail Particles
!!! tip "@snippet:versions:mc-1.21.4@"

Used for particles that create a colored trail to a target location.

```yaml
type: TRAIL
data:
  color: #(1)!
    r: 255
    g: 0
    b: 0
  duration: 20 #(2)!
  duration_unit: TICKS #(3)!
  target: "120;64;120;world" #(4)!
```

1.  **color**: The color of the trail.
2.  **duration**: The duration the trail takes to reach the target.
3.  **duration_unit**: The time unit for `duration`.
4.  **target**: The destination location.

---

## Shape Examples

Here are some examples of how to use mathematical functions to draw shapes.

### Vertical Line
A simple line that rises from 0 to 2 blocks in height.

=== "YAML"
    ```yaml
    functions:
      line_func: 'f(d) = d * 2' #(1)!
    
    particles:
      rising_line:
        type: HAPPY_VILLAGER
        animation:
          sampling:
            primary: 20 #(2)!
        shape:
          y: line_func #(3)!
    ```

    1.  **line_func**: `d` (d1) runs from 0 to 1. So the result is 0 to 2.
    2.  **primary**: 20 points are distributed evenly over the duration.
    3.  **y**: The height is determined by the function.
    
=== "Preview Line"
    <img src="../../../_media/content/Documentation/Advanced/Particles/vertical_line.png" width=450>

### Circle
A horizontal circle with a radius of 2 blocks.

=== "YAML"
    ```yaml
    functions:
      circle_x: 'f(d) = 2 * cos(d * 6.283)' #(1)!
      circle_z: 'f(d) = 2 * sin(d * 6.283)'
    
    particles:
      circle_effect:
        type: CLOUD
        animation:
          sampling:
            primary: 40 #(2)!
        shape:
          x: circle_x
          z: circle_z
          extra: 0
    ```

    1.  **6.283**: This corresponds to $2\pi$. Since `d` runs from 0 to 1, the function covers a full circle (360°).
    2.  **primary**: More points result in a smoother circle.

=== "Preview Circle"
    <img src="../../../_media/content/Documentation/Advanced/Particles/circle.png" width=450>

### Spiral
A spiral that screws itself upwards.

=== "YAML"
    ```yaml
    functions:
      spiral_x: 'f(d) = 2 * cos(d * 12.566)' #(1)!
      spiral_z: 'f(d) = 2 * sin(d * 12.566)'
      spiral_y: 'f(d) = d * 4' #(2)!
    
    particles:
      spiral_effect:
        type: FLAME
        animation:
          sampling:
            primary: 100
        shape:
          x: spiral_x
          y: spiral_y
          z: spiral_z
          extra: 0
    ```

    1.  **12.566**: This is $4\pi$, which corresponds to two full rotations.
    2.  **spiral_y**: While the particles rotate, they rise steadily to a height of 4 blocks.

=== "Preview Spiral"
    <img src="../../../_media/content/Documentation/Advanced/Particles/spiral.png" width=450>

---

## Advanced Animations

### Pulsing Ring (Secondary Sampling)
Here we use `secondary` to create a ring that expands and contracts several times.

=== "YAML"
    ```yaml
    functions:
      ring_x: 'f(d1, d2) = 2 * (1 + 0.5 * sin(d2 * 6.283)) * cos(d1 * 6.283)' #(1)!
      ring_z: 'f(d1, d2) = 2 * (1 + 0.5 * sin(d2 * 6.283)) * sin(d1 * 6.283)'
    
    particles:
      pulsing_ring:
        type: WAX_ON
        animation:
          thread: true
          duration: 2
          order: INWARD
          sampling:
            primary: 60
            secondary: 20
        shape:
          x: ring_x
          z: ring_z
          extra: 0
    ```

    1.  **d2**: In this example, we use `d2` to modulate the radius of the circle over time. As the animation jumps from point to point (`d1`), each point additionally runs through the `d2` cycle.

=== "Preview Ring"

    As this animation has a duration, this is only a snapshot of a single state during the animation.
    <img src="../../../_media/content/Documentation/Advanced/Particles/ring.png" width=450>

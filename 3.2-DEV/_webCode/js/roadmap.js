var FOCUSSED_TILE = null;
var FOCUS_UNLOCKED = true;
var LEFT_INFO_AREA_WIDTH = 0;
var RIGHT_INFO_AREA_WIDTH = 0;
var HEIGHT = 0;
var WIDTH = 0;
var SCALE = 1;
var ALL_CLUSTER;
var SITE_NESTING = "";

document$.subscribe(async () => {
  let scriptElement = document.getElementById("roadmapScript");
  if (!scriptElement) {
    return;
  }
  SITE_NESTING = scriptElement.getAttribute("nesting");
  let canvasElement = document.getElementById("roadmapCanvas");
  let ctx = canvasElement.getContext("2d");

  let params = new URLSearchParams(document.location.search);
  const SHOW_HISTORY = params.get("history") === "true";

  const DPR = window.devicePixelRatio;
  WIDTH = window.innerWidth * DPR;

  let imageCache = await loadImages(SITE_NESTING);
  let roadmap = await loadRoadmap(SITE_NESTING);
  let roadmapElements = roadmap["path"];
  let infoText = await loadInfoText(SITE_NESTING);
  let infoElements = infoText["info"];
  let styleElements = infoText.meta["styles"];

  const META = roadmap["meta"];
  SCALE = META["scale"] * window.innerWidth / window.innerHeight;
  const HEXAGON_SIZE = SCALE * DPR * META["cluster"]["tile-size"];
  const HISTORY = META["history"];

  let PATH_ELEMENTS = [];
  let startElement = null;
  let startLeft = HISTORY["start-orientation"] === "left";
  for (let i = 0; i < roadmapElements.length; i++) {
    PATH_ELEMENTS[i] = roadmapElements[i];
    PATH_ELEMENTS[i]["pos"] = i;
    PATH_ELEMENTS[i]["left"] = i % 2 === roadmapElements.length % 2 ? !startLeft : startLeft;
    if (PATH_ELEMENTS[i]["id"] === HISTORY["start"]) {
      startElement = PATH_ELEMENTS[i];
      if (!SHOW_HISTORY) {
        break;
      }
    }
  }

  HEIGHT = PATH_ELEMENTS.length * HEXAGON_SIZE * 2.5;
  if (!SHOW_HISTORY) {
    HEIGHT += HISTORY["button"]["height"] + HISTORY["button"]["top-margin"];
  }
  const BASE_HEIGHT = HEXAGON_SIZE * 1.30 + META["margin"]["top"];
  const BASE_LEFT = WIDTH / 2 + META["margin"]["left"];
  const LEFT_ALIGNMENT = BASE_LEFT - HEXAGON_SIZE * META["cluster"]["left-scale"];
  const RIGHT_ALIGNMENT = BASE_LEFT - HEXAGON_SIZE * META["cluster"]["right-scale"];
  const HEIGHT_OFFSET = HEXAGON_SIZE * META["cluster"]["height-offset-scale"];
  const CLUSTER_CENTER_OFFSET_SCALE = META["cluster"]["center-offset"]["scale"];
  const HOVER_FILTER = META["hover-filter"];

  LEFT_INFO_AREA_WIDTH = LEFT_ALIGNMENT - HEXAGON_SIZE * 1.55;
  RIGHT_INFO_AREA_WIDTH = RIGHT_ALIGNMENT + HEXAGON_SIZE * 1.45;

  const historyButton = {
    data: HISTORY["button"],
    x: BASE_LEFT - HISTORY["button"]["width"] / 2,
    y: HEIGHT - HISTORY["button"]["height"] - HISTORY["button"]["line-width"],
    width: HISTORY["button"]["width"],
    height: HISTORY["button"]["height"],
    hovered: false,
    click: function () {
      window.location.href = "?history=true";
    },
    draw: function (ctx) {
      ctx.save();
      ctx.fillStyle = this.hovered ? this.data["hover-color"] : this.data["background-color"];
      ctx.strokeStyle = this.data["line-color"];
      ctx.lineWidth = this.data["line-width"];
      ctx.fillRect(this.x, this.y, this.width, this.height);
      ctx.strokeRect(this.x, this.y, this.width, this.height);
      ctx.fillStyle = this.data["font-color"];
      ctx.font = this.data["font-size"] + "px " + this.data["font-family"];
      ctx.textAlign = "center";
      ctx.textBaseline = "middle";
      ctx.fillText(this.data["text"], this.x + this.width / 2, this.y + this.height / 2, this.width);
      ctx.restore();
    },
    hit: function (point) {
      return point.x >= this.x && point.x <= this.x + this.width
        && point.y >= this.y && point.y <= this.y + this.height;
    }
  };

  const SETTINGS = {
    align: {
      left: LEFT_ALIGNMENT,
      right: RIGHT_ALIGNMENT,
    },
    hover: {
      filter: HOVER_FILTER,
    },
    base: {
      size: HEXAGON_SIZE,
      left: BASE_LEFT,
      top: BASE_HEIGHT,
    },
    cluster: {
      offset: HEIGHT_OFFSET,
      scale: CLUSTER_CENTER_OFFSET_SCALE
    }
  };

  canvasElement.width = WIDTH;
  canvasElement.height = HEIGHT;

  let canvasStyle = getComputedStyle(canvasElement);
  let canvasWidth = parseFloat(canvasStyle.width);
  let canvasHeight = parseFloat(canvasStyle.height);
  canvasElement.display = {width: canvasWidth, height: canvasHeight};

  let styleCache = buildStyleCache(styleElements);
  let infoCache = buildInfoCache(infoElements, styleCache);
  let clusters = await buildRoadmap(ctx, PATH_ELEMENTS, SETTINGS, imageCache, infoCache);

  ALL_CLUSTER = clusters;
  drawRoadmap(ctx);

  if (!SHOW_HISTORY && roadmapElements.length > PATH_ELEMENTS.length) {
    historyButton.draw(ctx);
  }

  canvasElement.onmousemove = function (e) {
    let rect = this.getBoundingClientRect(),
      x = e.clientX - rect.left,
      y = e.clientY - rect.top;

    let point = {
      x: x * canvasElement.width / rect.width,
      y: y * canvasElement.height / rect.height
    };
    onMouseMove(ctx, clusters, point);

    if (!SHOW_HISTORY && roadmapElements.length > PATH_ELEMENTS.length) {
      let previous = historyButton.hovered;
      historyButton.hovered = historyButton.hit(point);
      if (previous !== historyButton.hovered) {
        historyButton.draw(ctx);
      }
    }
  };

  canvasElement.onclick = function (e) {
    if (FOCUSSED_TILE != null && FOCUSSED_TILE.info !== undefined && FOCUS_UNLOCKED) {
      FOCUSSED_TILE.info.click();
    } else {
      let rect = this.getBoundingClientRect(),
        x = e.clientX - rect.left,
        y = e.clientY - rect.top;
      if (!SHOW_HISTORY && roadmapElements.length > PATH_ELEMENTS.length && historyButton.hit({
        x: x * canvasElement.width / rect.width,
        y: y * canvasElement.height / rect.height
      })) {
        historyButton.click();
      }
    }
  };

  let startCluster = ALL_CLUSTER.find(cluster => cluster["id"] === startElement["id"]);

  let targetY = scrollToCluster(startCluster, canvasElement);
  scrollTo({left: 0, top: targetY, behavior: "smooth"});

  onscroll = (e) => {
    if (FOCUSSED_TILE != null && FOCUSSED_TILE.info !== undefined) {
      clearInfoArea(ctx).then(() => FOCUSSED_TILE.info.draw(ctx));
    }
  };

});

function drawRoadmap(ctx) {
  ALL_CLUSTER.forEach(cluster => {
    cluster.draw(ctx);
  });
}

async function clearInfoArea(ctx) {
  ctx.clearRect(0, 0, LEFT_INFO_AREA_WIDTH, HEIGHT);
  ctx.clearRect(RIGHT_INFO_AREA_WIDTH, 0, WIDTH, HEIGHT);
}

function scrollToCluster(cluster, canvasElement) {
  let canvasBounds = canvasElement.getBoundingClientRect();
  let y = cluster.tiles[0].hex.y - canvasBounds.y - canvasBounds.height / 2;
  return y * canvasBounds.height / canvasBounds.height;
}

async function unfocusTile(ctx, tile) {
  await clearInfoArea(ctx);
  if (tile != null) {
    //tile.draw(ctx);
  }
  drawRoadmap(ctx);
}

async function focusTile(ctx, tile) {
  //tile.draw(ctx);
  drawRoadmap(ctx);
  if (tile.info !== undefined) {
    tile.info.draw(ctx);
  }
}

async function onMouseMove(ctx, clusters, mouse) {
  let previous = FOCUSSED_TILE;
  let anyHit = false;
  clusters.map(async cluster => {
    cluster.tiles.map(async tile => {
      if (tile.hit(mouse)) {
        if (anyHit) {
          return;
        }
        anyHit = true;
        if (FOCUSSED_TILE != null && FOCUSSED_TILE.id === tile.id) {
          return;
        }
        FOCUSSED_TILE = tile;
        FOCUS_UNLOCKED = false;
        setTimeout(() => FOCUS_UNLOCKED = true, 100);
        await unfocusTile(ctx, previous);
        await focusTile(ctx, FOCUSSED_TILE);
      }
    });
  });
  if (!anyHit) {
    FOCUSSED_TILE = null;
    await unfocusTile(ctx, previous);
  }
}

function buildStyleCache(elements) {
  let styleCache = new Map();
  Object.entries(elements).forEach(([id, style]) => {
    styleCache.set(id, style);
  });
  return styleCache;
}

function buildInfoCache(elements, styleCache) {
  let infoCache = new Map();
  for (let element of elements) {
    let id = element.id;
    let style = styleCache.get(element.style);
    let tiles = element.tiles;
    Object.entries(tiles).forEach(([orientation, info]) => {
      let infoId = id + ":" + orientation;
      let infoBox = buildTextBox(style, info);
      infoCache.set(infoId, infoBox);
    });
  }
  return infoCache;
}

async function buildRoadmap(ctx, elements, settings, imageCache, infoCache) {
  return Promise.all(elements.map(async elements => {
    let id = elements.id;
    let hexagonTiles = await buildTileHexagon(id, infoCache,
      elements.left ? settings.align.left : settings.align.right,
      settings.base.top + settings.cluster.offset * elements.pos,
      settings.base.size, elements.tiles, elements.decorations, imageCache, settings.cluster.scale,
      settings.hover.filter);
    return {
      id: id,
      tiles: hexagonTiles,
      draw: async function (ctx) {
        return Promise.all(this.tiles.map(async tile => tile.draw(ctx)))
          .then(() => Promise.all(this.tiles.map(async tile => tile.drawDecoration(ctx))));
      }
    };
  }));
}

async function buildTileHexagon(id, infoCache, x, y, size, tiles, decorations, imageCache, offsetScales, hoverFilter) {
  return Promise.all(Object.entries(tiles).map(async ([orientation, tile]) => {
    return mapOrientation(orientation, size, offsetScales)
      .then(offsets => buildTile(x + offsets.x, y + offsets.y, size, tile, decorations ? decorations[orientation] : undefined,
        imageCache, id + ":" + orientation, infoCache, hoverFilter));
  }));
}

async function buildTile(x, y, size, imageId, decoration, imageCache, identifier, infoCache, hoverFilter) {
  let hex = await buildHex(x, y, size);
  let tileInfo = infoCache.get(identifier);
  return {
    id: identifier,
    filter: hoverFilter,
    hex: hex,
    deco: decoration,
    info: tileInfo,
    draw: async function (ctx) {
      return drawHexTile(ctx, this.id, this.hex, imageCache.get(imageId), imageCache, this.filter);
    },
    drawDecoration: async function (ctx) {
      if (!this.deco) {
        return;
      }
      return drawTileDecoration(ctx, this.id, this.hex, this.deco, imageCache.get(this.deco.asset), this.filter);
    },
    hit: function (point) {
      let previous = hex.start;
      let valid = true;
      for (let vertex of hex.vertices) {
        if (cross(point, previous, vertex) < 0) {
          valid = false;
        }
        previous = vertex;
      }
      if (cross(point, previous, hex.start) < 0) {
        valid = false;
      }
      return valid;
    }
  };
}

function cross(point, start, end) {
  return (end.x - start.x) * (point.y - start.y)
    - (end.y - start.y) * (point.x - start.x);
}

async function buildHex(x, y, size) {
  return {
    x: x,
    y: y,
    size: size,
    bottom: {
      x: x + size / 2,
      y: y + size / 2
    },
    top: {
      x: x - size / 2,
      y: y - size / 2
    },
    start: {
      x: x,
      y: y - size / 2
    },
    vertices: [
      {x: x + size / 2, y: y - size / 4},
      {x: x + size / 2, y: y + size / 4},
      {x: x, y: y + size / 2},
      {x: x - size / 2, y: y + size / 4},
      {x: x - size / 2, y: y - size / 4}
    ],
  };
}

async function drawTileDecoration(ctx, id, hex, deco, imageBox, hoverFilter) {
  let targetX = hex.x - hex.size / 2 + (imageBox.offset.x + deco.offset.x) * window.devicePixelRatio * SCALE;
  let targetY = hex.y - hex.size / 2 + (imageBox.offset.y + deco.offset.y) * window.devicePixelRatio * SCALE;
  ctx.save();
  if (!deco.hasOwnProperty("clip") || deco.clip) {
    clipHex(ctx, hex, deco.offset);
  }
  if (FOCUSSED_TILE != null && FOCUSSED_TILE.id === id) {
    ctx.filter = FOCUSSED_TILE.info ? hoverFilter.content : hoverFilter.empty;
  }
  ctx.drawImage(imageBox.image, 0, 0, imageBox.size.width, imageBox.size.height, targetX, targetY, hex.size, hex.size);
  ctx.restore();
}

async function drawHexTile(ctx, id, hex, imageBox, imageCache, hoverFilter) {
  if (imageBox.hasOwnProperty("base")) {
    await drawHexTile(ctx, id, hex, imageCache.get(imageBox.base), imageCache, hoverFilter);
  }
  let targetX = hex.x - hex.size / 2 + imageBox.offset.x * window.devicePixelRatio * SCALE;
  let targetY = hex.y - hex.size / 2 + imageBox.offset.y * window.devicePixelRatio * SCALE;
  ctx.save();
  clipHex(ctx, hex, {x: 0, y: 0});
  if (FOCUSSED_TILE != null && FOCUSSED_TILE.id === id) {
    ctx.filter = FOCUSSED_TILE.info ? hoverFilter.content : hoverFilter.empty;
  }
  ctx.drawImage(imageBox.image, 0, 0, imageBox.size.width, imageBox.size.height, targetX, targetY, hex.size, hex.size);
  ctx.restore();
}

function clipHex(ctx, hex, offset) {
  ctx.beginPath();
  ctx.moveTo(hex.start.x + offset.x, hex.start.y + offset.y);
  for (let vertex of hex.vertices) {
    ctx.lineTo(vertex.x + offset.x, vertex.y + offset.y);
  }
  ctx.closePath();
  ctx.clip();
}

async function mapOrientation(orientation, size, scales) {
  const dx = size * scales.x;
  const dy = size * scales.y;
  switch (orientation) {
    case "center":
      return {x: 0, y: 0};
    case "east":
      return {x: dx, y: 0};
    case "west":
      return {x: -dx, y: 0};
    case "north-east":
      return {x: dx / 2, y: -dy};
    case "north-west":
      return {x: -dx / 2, y: -dy};
    case "south-east":
      return {x: dx / 2, y: dy};
    case "south-west":
      return {x: -dx / 2, y: dy};
    default:
      console.error("Invalid orientation: " + orientation);
      return {x: 0, y: 0};
  }
}

async function loadRoadmap(nesting) {
  const roadmap = await fetch(nesting + "_roadmap/roadmap.json");
  return roadmap.json();
}

async function loadInfoText(nesting) {
  const infoText = await fetch(nesting + "_roadmap/info-text.json");
  return infoText.json();
}

async function loadImages(nesting) {
  const assets = await fetch(nesting + "_roadmap/assets.json");
  let assetJson = await assets.json();
  const imageCache = new Map();
  await Promise.all(
    Object.entries(assetJson).map(async ([key, value]) => {
      let image = null;
      if (value.hasOwnProperty("url")) {
        image = await loadImage(value.url);
      } else if (value.hasOwnProperty("path")) {
        image = await loadImage(nesting + value.path);
      }
      if (image == null) {
        console.error("Failed to load image: " + key);
        return;
      }
      let offset = value.hasOwnProperty("offset") ? value.offset : {x: 0, y: 0};
      if (value.hasOwnProperty("base")) {
        imageCache.set(key, {image: image, offset: offset, size: value.size, base: value.base});
      } else {
        imageCache.set(key, {image: image, offset: offset, size: value.size});
      }
    }));
  return imageCache;
}

async function loadImage(src) {
  return new Promise(resolve => {
    const img = new Image();
    img.onload = () => resolve(img);
    img.src = src;
  });
}

function buildTextBox(style, info) {
  return {
    info: info,
    style: style,
    draw: async function (ctx) {
      let rect = ctx.canvas.getBoundingClientRect();
      let ratioX = WIDTH / ctx.canvas.display.width;
      let ratioY = HEIGHT / ctx.canvas.display.height;

      let left = ratioX * (-rect.x + ctx.canvas.getBoundingClientRect().x + window.scrollX);
      let top = ratioY * (-rect.y + ctx.canvas.getBoundingClientRect().y + window.scrollY);
      let innerHeight = window.innerHeight < rect.height ? window.innerHeight : rect.height;
      let innerWidth = window.innerWidth < rect.width ? window.innerWidth : rect.width;
      let width = innerWidth * ratioX;
      let height = innerHeight * ratioY;

      let posX = left + width * style.position["x"];
      let posY = top + height * style.position["y"];
      let w = width * style.box.size["width"];
      let h = height * style.box.size["height"];
      if (posX + w + style.box["line-width"] >= LEFT_INFO_AREA_WIDTH) {
        w = LEFT_INFO_AREA_WIDTH - style.box["line-width"] - posX - 1;
      }

      await drawBox(ctx, posX, posY, w, h, this.style.box);
      await drawText(ctx, this.info, posX, posY, w, h, this.style.title, this.style.features);
    },
    click: function () {
      if (!this.info.link) {
        return;
      }
      window.open(SITE_NESTING + this.info.link, "_blank");
    }
  };
}

function splitLine(ctx, text, maxWidth) {
  let textMetrics = ctx.measureText(text);
  if (textMetrics.width <= maxWidth) {
    return [text];
  }
  let words = text.split(" ");
  let lines = [];
  let currentLine = "";
  for (let i = 0; i < words.length; i++) {
    let word = words[i];
    let width = ctx.measureText(currentLine + word + " ").width;
    if (width < maxWidth) {
      currentLine += word + " ";
    } else {
      lines.push(currentLine);
      currentLine = "  " + word + " ";
    }
  }
  if (currentLine.trim() !== "") {
    lines.push(currentLine);
  }

  return lines;
}

async function drawText(ctx, info, x, y, boxWidth, boxHeight, titleStyle, featuresStyle) {
  ctx.save();

  ctx.font = titleStyle["font-size"] + "px " + titleStyle["font-family"];
  ctx.fillStyle = titleStyle["color"];
  ctx.textAlign = "left";
  ctx.textBaseline = "middle";
  let sizeChange = 0;
  while (ctx.measureText(info.title).width >= boxWidth - titleStyle.offset.x * 2) {
    sizeChange++;
    ctx.font = (titleStyle["font-size"] - sizeChange) + "px " + titleStyle["font-family"];
  }
  ctx.fillText(info.title, x + titleStyle.offset.x, y + titleStyle.offset.y);
  let sizeMetrics = ctx.measureText("yM");
  let titleHeight = sizeMetrics.actualBoundingBoxAscent + sizeMetrics.actualBoundingBoxDescent;

  let offsets = {
    x: featuresStyle.offset.x,
    y: featuresStyle.offset.y + titleStyle.offset.y + titleHeight
  };

  ctx.font = featuresStyle["font-size"] + "px " + featuresStyle["font-family"];
  ctx.fillStyle = featuresStyle["color"];
  ctx.textAlign = "left";
  ctx.textBaseline = "middle";
  let textMetrics = ctx.measureText("yM");
  let height = textMetrics.actualBoundingBoxAscent + textMetrics.actualBoundingBoxDescent;
  let lineSpacing = featuresStyle["line-spacing"];
  let lineMaxWidth = boxWidth - featuresStyle.offset.x * 2;

  let features = info.features.slice(0);
  if (info.link) {
    let clickInfo = info["link-text"] ? info["link-text"] : "Click the block for more information.";
    features.push("");
    features.push(clickInfo);
  }

  let maxLines = 0;
  let text = [];
  sizeChange = 0;
  do {
    ctx.font = (featuresStyle["font-size"] - sizeChange) + "px " + featuresStyle["font-family"];
    maxLines = Math.floor((boxHeight - offsets.y) / (height + height * lineSpacing));
    text = [];
    for (let feature of features) {
      let lines = splitLine(ctx, feature, lineMaxWidth);
      for (let line of lines) {
        text.push(line);
      }
    }
    sizeChange++;
  } while (text.length > maxLines && sizeChange < featuresStyle["font-size"] / 2);

  let removed = false;
  let last = null;
  if (text.length > maxLines) {
    while (text.length >= maxLines) {
      let index = text.lastIndexOf("");
      if (index > 0) {
        text.splice(index - 1, 1);
        last = index - 1;
      } else {
        text.splice(text.length - 1, 1);
        last = text.length;
      }
      removed = true;
    }
  }

  if (removed && last != null) {
    text.splice(last, 0, "... and more");
  }

  let lastIndex = text.lastIndexOf("");
  if (lastIndex > 0) {
    console.log(text.length, maxLines, lastIndex);
    while (text.length < maxLines) {
      text.splice(lastIndex, 0, "");
    }
  }

  for (let i = 0; i < text.length; i++) {
    ctx.fillText(text[i], x + offsets.x, y + offsets.y + (height + height * lineSpacing) * i);
  }

  ctx.restore();
}

async function drawBox(ctx, x, y, w, h, style) {
  ctx.save();
  ctx.strokeStyle = style["line-color"];
  ctx.fillStyle = style["background-color"];
  ctx.lineWidth = style["line-width"];
  ctx.fillRect(x, y, w, h);
  ctx.strokeRect(x, y, w, h);
  ctx.restore();
}

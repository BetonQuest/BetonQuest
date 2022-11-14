updateRating()

async function updateRating() {
  let betonQuest = getRating(2117)
  let quests = getRating(3711)
  let LMBquests = getRating(23696)
  let notQuests = getRating(95872)
  let beautyQuests = getRating(39255)
  let mangoQuest = new Promise((resolve) => {
    /* MangoQuest is not on Spigot */
    resolve("-")
  })
  let questCreator = getRating(38734)
  let proQuests = getRating(18249)

  const ratings = Promise.all([betonQuest, quests, LMBquests, notQuests, beautyQuests, mangoQuest, questCreator, proQuests])

  // Get the first row of the table, excluding the first cell (the description)
  let firstTableRow = document.querySelectorAll("table > tbody > tr:nth-child(1) > td:not(:first-child)");

  ratings.then((values) => {
    for (let i = 0; i < values.length; i++) {
      firstTableRow.item(i).textContent = values[i] + " ⭐"
    }
  })
}

function getRating(ressourceID) {
  return fetch("https://api.spiget.org/v2/resources/" + ressourceID).then(response => response.json())
    .then(data => data["rating"]["average"])
}


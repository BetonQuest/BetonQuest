updateRating()

async function updateRating() {
  let betonQuest = await getRating(2117)
  let quests = await getRating(3711)
  let LMBquests = await getRating(23696)
  let notQuests = await getRating(95872)
  let beautyQuests = await getRating(39255)
  // Not distributed via Spigot
  let mangoQuest = "N/A"
  let questCreator = await getRating(38734)
  let proQuests = await getRating(18249)

  let ratings = [betonQuest, quests, LMBquests, notQuests, beautyQuests, mangoQuest, questCreator, proQuests]

  // Get the first row of the table, excluding the first cell (the description)
  let firstTableRow = document.querySelectorAll("table > tbody > tr:nth-child(1) > td:not(:first-child)");
  for (let i = 0; i < ratings.length; i++) {
    firstTableRow.item(i).textContent = ratings[i] + " ⭐"
  }
}

async function getRating(ressourceID) {
  return await fetch("https://api.spiget.org/v2/resources/" + ressourceID).then(response => response.json())
    .then(data => {
      return data["rating"]["average"]
    })
}


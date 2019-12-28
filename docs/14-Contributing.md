# Contributing

Do you like my work here? There are some ways you can help to make this plugin even better:

## New ideas

Need something? Or just have a brilliant idea? Head to the [Issues](https://github.com/Co0sh/BetonQuest/issues) and create new one. Just remember to start the title with uppercase letter or I will edit it!

## Bug reports

Found a bug? Great, create new [issue](https://github.com/Co0sh/BetonQuest/issues) so I can fix it in the next version!

## Translations

I love to see this plugin used by people from other countries. I would be happy if you could translate it to your language and share the translation with me. You can send me the edited _messages.yml_ file or submit a pull request.

## Contributing code

If you know Java and Bukkit you can take some issue and create pull request. Just let me know and remember these few things:

* The contributed code should be well tested and fully working.
* Use only spaces for indentation.
* Wrap your code at 120th character.
* Comment everything so the code is easy to understand for everyone.
* Use block comments to document classes, methods and fields.

## Contributing documentation

If you can help improve the documentation it would be highly appreciated. Have a look under the `docs` folder for the existing documentation.

Documentation is built using `mkdocs`. You can set up an hot-build dev environment that will auto-refresh changes as they are made.

### Requirements

* python3
* pip3
* npm (only if changing themes)

Install dependencies by running:

```
pip3 install -r requirements.txt
```

### Dev Environment

To start an http document server on `http://127.0.0.1:8000` execute:

```
mkdocs serve
```

### Change PDF Theme

Edit the PDF theme under `design/pdf`. Rebuild by doing the following:

```
cd design/pdf
npm install
npm run build-compressed
```

This will update `pdf.css` under `docs/css/pdf.css`. Rebuilding the docs will now use the new theme.

## Positive feedback

I really like to hear that people are using my plugin. If you've got a server and have made a few quests just let me know so I can check it out ^^

## Donations

If you have some spare money and REALLY like this plugin you can donate [here](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=KG6S76KP4W6UG). This project however is not dependent on donations, so it's really optional :)

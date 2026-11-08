class Badge:
    def __init__(self, icon: str, text: str):
        self.icon = icon
        self.text = text

    def get_badge(self) -> str:
        return "".join([
            f"<span class=\"bq-badge\">",
            *([f"<span class=\"bq-badge__icon\">{self.icon}</span>"] if self.icon else []),
            *([f"<span class=\"bq-badge__text\">{self.text}</span>"] if self.text else []),
            f"</span>",
        ])

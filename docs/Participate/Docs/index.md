??? info "Hosting on your entire local network"
    You can also execute this variation to host the website in your local network.
    This can be useful for testing changes on different devices but is not needed for most tasks.
    Make sure the hosting device's firewall exposes the port 8000.
    ```BASH
    mkdocs serve -a 0.0.0.0:8000
    ```

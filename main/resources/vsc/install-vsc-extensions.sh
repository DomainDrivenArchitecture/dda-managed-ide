# install_VSC_extensions

#clojure
code --install-extension betterthantomorrow.calva
code --install-extension martinklepsch.clojure-joker-linter
code --install-extension DavidAnson.vscode-markdownlint
curl -Lo joker-0.12.2-linux-amd64.zip https://github.com/candid82/joker/releases/download/v0.12.2/joker-0.12.2-linux-amd64.zip
unzip joker-0.12.2-linux-amd64.zip
sudo mv joker /usr/local/bin/

# python
code --install-extension ms-python.python

# git
code --install-extension eamodio.gitlens

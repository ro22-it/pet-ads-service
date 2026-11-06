# КТ Linux — как выполнить

1. Скачать и установить Oracle VirtualBox: https://www.virtualbox.org
   (на Mac с Apple Silicon — версия для «macOS / Apple Silicon hosts»).
2. Скачать образ Ubuntu Desktop 26.04 LTS, образ ubuntu-26.04.1-desktop-arm64.iso (для Apple Silicon).
3. В VirtualBox: «Создать» → указать ISO → 2 CPU, 4096 МБ, диск 25 ГБ → установить Ubuntu,
   при установке создать пользователя.
4. Открыть Terminal и выполнять команды из [commands.sh](commands.sh) по одной.
5. Скриншоты сохранять в `screenshots/` с именами из [linux-report.md](linux-report.md):
   `01-ubuntu.png` … `07-history.png`.
6. Проверить, что в отчёте совпадают имя пользователя и версия Ubuntu, и сдать `linux-report.md`
   (или распечатать в PDF).

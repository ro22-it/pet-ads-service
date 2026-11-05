#!/bin/bash
# КП Linux: команды выполняются по одной в терминале Ubuntu,
# после каждого блока с пометкой [скрин] сделать скриншот окна.

# 1. Где мы и кто мы
whoami
pwd

# 2. Папка для проектов и папка проекта
mkdir -p ~/projects/pet-ads-service
cd ~/projects/pet-ads-service
pwd                                   # [скрин] путь к рабочей папке

# 3. Файл с несколькими строками
echo "Pet Ads Service - pet adoption ads" > notes.txt
echo "Entity: Animal (name, type, breed, age, price)" >> notes.txt
echo "Next step: API in Docker container" >> notes.txt
cat notes.txt

# 4. README проекта
echo "# Pet Ads Service" > README.md
echo "Study project: pet adoption ads service" >> README.md
cat README.md

# 5. Копия файла и переименование копии
cp notes.txt notes-copy.txt
mv notes-copy.txt notes-backup.txt
ls                                    # [скрин] файлы проекта

# 6. Права доступа
ls -la                                # [скрин] права до изменения
chmod 600 notes-backup.txt            # только владелец: чтение и запись
chmod 755 README.md                   # владелец всё, остальные чтение и запуск
ls -la                                # [скрин] права после изменения

# 7. Установка пакета через apt
sudo apt update
sudo apt install -y curl              # [скрин] установка
curl --version

# 8. История команд
history | tail -n 30                  # [скрин] история

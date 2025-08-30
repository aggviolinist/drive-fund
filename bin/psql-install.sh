#!/usr/bin/env bash
set -e  # exit if any command fails

echo "🔄 Updating apt packages..."
sudo apt-get update -y

echo "📦 Installing PostgreSQL client..."
sudo apt-get install -y postgresql-client

echo "✅ Installed psql version:"
psql --version
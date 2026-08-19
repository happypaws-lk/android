/**
 * HappyPaws.lk - APK Download Landing Page Script
 * Dynamically queries GitHub Releases to fetch the latest APK build & version metadata.
 */

document.addEventListener('DOMContentLoaded', () => {
  fetchLatestRelease();
});

async function fetchLatestRelease() {
  const downloadBtn = document.getElementById('apk-download-btn');
  const versionMeta = document.getElementById('apk-version-meta');
  const heroVersionTag = document.getElementById('hero-version-tag');
  const btnLabel = document.getElementById('btn-label-text');

  const repo = 'happypaws-lk/android';
  let releaseData = null;

  try {
    const response = await fetch(`https://api.github.com/repos/${repo}/releases/latest`, {
      headers: {
        'Accept': 'application/vnd.github.v3+json'
      }
    });

    if (response.ok) {
      releaseData = await response.json();
    }
  } catch (e) {
    // Network error or offline
  }

  if (!releaseData) {
    if (versionMeta) {
      versionMeta.textContent = 'Universal Production Build (APK)';
    }
    return;
  }

  const tagName = releaseData.tag_name || 'v1.0.0';
  const assets = releaseData.assets || [];
  const apkAsset = assets.find(a => a.name && a.name.toLowerCase().endsWith('.apk'));

  if (heroVersionTag) {
    heroVersionTag.textContent = `Android App • ${tagName}`;
  }

  if (apkAsset) {
    const sizeInMB = (apkAsset.size / (1024 * 1024)).toFixed(1);
    if (downloadBtn) {
      downloadBtn.href = apkAsset.browser_download_url;
      downloadBtn.setAttribute('download', apkAsset.name);
    }
    if (btnLabel) {
      btnLabel.textContent = `Download APK (${tagName})`;
    }
    if (versionMeta) {
      versionMeta.textContent = `Direct Install • ${sizeInMB} MB • ${formatDate(releaseData.published_at)}`;
    }
  } else {
    if (downloadBtn && releaseData.html_url) {
      downloadBtn.href = releaseData.html_url;
    }
    if (versionMeta) {
      versionMeta.textContent = `Release ${tagName} • GitHub`;
    }
  }
}

function formatDate(isoString) {
  if (!isoString) return '';
  const date = new Date(isoString);
  return date.toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric'
  });
}

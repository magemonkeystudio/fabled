import { expect, test } from '@playwright/test';

test('home page has expected headings', async ({ page }) => {
	await page.goto('/');
	await expect(page.locator('h1')).toBeVisible();
	await expect(page.locator('h1')).toHaveText('Fabled');
	await expect(page.locator('h2')).toHaveText('Dynamic Editor');
});

test('clicking New Class adds an entry to the list', async ({ page }) => {
	await page.goto('/');
	await page.getByRole('button', { name: 'New Class' }).click();
	await expect(page.locator('css=.sidebar-entry')).toHaveCount(2);
	await expect(page.locator('css=.sidebar-entry:first-child')).toContainText('Class 1');
	// Test local storage
	const classes = await page.evaluate(val => localStorage.getItem('classNames'));
	expect(classes).toBe('Class 1');
	const class1 = await page.evaluate(val => localStorage.getItem('sapi.class.Class 1'));
	expect(class1).toBeDefined();
	await page.reload();
	await expect(page.locator('css=.sidebar-entry')).toHaveCount(2);
	await expect(page.locator('css=.sidebar-entry:first-child')).toContainText('Class 1');
});
